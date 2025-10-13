package dev.sqrilizz.SQRILIZZREPORTS.api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import dev.sqrilizz.SQRILIZZREPORTS.Main;
import dev.sqrilizz.SQRILIZZREPORTS.ReportManager;
import dev.sqrilizz.SQRILIZZREPORTS.errors.ErrorManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

/**
 * Embedded REST server for two-way integration with Discord bot and external systems.
 */
public class RESTServer {
    private static HttpServer server;
    private static final Gson gson = new Gson();

    // Simple rate limiting: requests per IP per second
    private static int RATE_LIMIT_RPS = 10;
    private static final Map<String, RateBucket> buckets = new ConcurrentHashMap<>();

    public static void initialize() {
        var cfg = Main.getInstance().getConfig();
        boolean enabled = cfg.getBoolean("rest-api.enabled", false);
        if (!enabled) {
            Main.getInstance().getLogger().info("REST API is disabled in config");
            return;
        }
        int port = cfg.getInt("rest-api.port", 8971);
        RATE_LIMIT_RPS = Math.max(1, cfg.getInt("rest-api.rate-limit.rps", 10));
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/api/reports", new ReportsHandler());
            server.createContext("/api/stats", new StatsHandler());
            server.setExecutor(Executors.newCachedThreadPool(r -> {
                Thread t = new Thread(r, "SQRILIZZ-REST");
                t.setDaemon(true);
                return t;
            }));
            server.start();
            Main.getInstance().getLogger().info("REST API listening on port " + port);
        } catch (IOException e) {
            ErrorManager.logError("REST_INIT", e);
        }
    }

    public static void shutdown() {
        if (server != null) {
            server.stop(0);
            server = null;
        }
    }

    private static class ReportsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if (!rateLimit(exchange)) return;
                String method = exchange.getRequestMethod();
                URI uri = exchange.getRequestURI();
                String path = uri.getPath();
                String[] parts = path.split("/"); // /api/reports/{player} or /api/reports/{id}/resolve

                // Auth checks
                String auth = exchange.getRequestHeaders().getFirst("Authorization");
                if (!AuthManager.checkBearer(auth) || !AuthManager.checkIp(exchange.getRemoteAddress())) {
                    writeJson(exchange, 401, AuthManager.errorJson("Unauthorized"));
                    return;
                }

                if ("GET".equals(method)) {
                    if (parts.length == 3) {
                        // /api/reports
                        handleGetAll(exchange);
                        return;
                    } else if (parts.length == 4) {
                        // /api/reports/{player}
                        String player = urlDecode(parts[3]);
                        handleGetPlayer(exchange, player);
                        return;
                    }
                } else if ("POST".equals(method)) {
                    String body = readBody(exchange);
                    String signature = exchange.getRequestHeaders().getFirst("X-Signature");
                    if (!AuthManager.verifyHmac(body, signature)) {
                        writeJson(exchange, 401, AuthManager.errorJson("Invalid signature"));
                        return;
                    }
                    if (parts.length == 5 && "resolve".equals(parts[4])) {
                        // /api/reports/{id}/resolve
                        String idStr = parts[3];
                        handleResolve(exchange, idStr);
                        return;
                    } else if (parts.length == 5 && "reply".equals(parts[4])) {
                        // /api/reports/{id}/reply
                        String idStr = parts[3];
                        handleReply(exchange, idStr, body);
                        return;
                    }
                }
                writeJson(exchange, 404, AuthManager.errorJson("Not found"));
            } catch (Exception e) {
                ErrorManager.logError("REST_REPORTS", e);
                writeJson(exchange, 500, AuthManager.errorJson("Internal error"));
            }
        }

        private void handleGetAll(HttpExchange exchange) throws IOException {
            Map<String, List<ReportManager.Report>> all = ReportManager.getReports();
            JsonArray arr = new JsonArray();
            for (var entry : all.entrySet()) {
                String player = entry.getKey();
                JsonObject o = new JsonObject();
                o.addProperty("player", player);
                JsonArray reports = new JsonArray();
                for (var r : entry.getValue()) {
                    reports.add(toJson(r));
                }
                o.add("reports", reports);
                arr.add(o);
            }
            writeJson(exchange, 200, arr);
        }

        private void handleGetPlayer(HttpExchange exchange, String player) throws IOException {
            var list = CacheManager.getReportsCached(player);
            JsonObject o = new JsonObject();
            o.addProperty("player", player);
            JsonArray reports = new JsonArray();
            for (var r : list) reports.add(toJson(r));
            o.add("reports", reports);
            writeJson(exchange, 200, o);
        }

        private void handleResolve(HttpExchange exchange, String idStr) throws IOException {
            long id = parseLong(idStr, -1);
            if (id <= 0) {
                writeJson(exchange, 400, AuthManager.errorJson("Invalid id"));
                return;
            }
            boolean ok = ReportManager.resolveReport(id, "API");
            if (ok) {
                CacheManager.invalidateAll();
                writeJson(exchange, 200, message("resolved"));
            } else {
                writeJson(exchange, 404, AuthManager.errorJson("Report not found"));
            }
        }

        private void handleReply(HttpExchange exchange, String idStr, String body) throws IOException {
            long id = parseLong(idStr, -1);
            if (id <= 0) {
                writeJson(exchange, 400, AuthManager.errorJson("Invalid id"));
                return;
            }
            JsonObject json = safeParse(body);
            String message = json != null && json.has("message") ? json.get("message").getAsString() : null;
            String author = json != null && json.has("author") ? json.get("author").getAsString() : "API";
            if (message == null || message.trim().isEmpty()) {
                writeJson(exchange, 400, AuthManager.errorJson("Missing message"));
                return;
            }
            boolean ok = ReportManager.addReply(id, author, message);
            if (ok) {
                CacheManager.invalidateAll();
                writeJson(exchange, 200, message("replied"));
            } else {
                writeJson(exchange, 404, AuthManager.errorJson("Report not found"));
            }
        }

        private JsonObject toJson(ReportManager.Report r) {
            JsonObject o = new JsonObject();
            o.addProperty("id", r.id);
            o.addProperty("reporter", r.reporter);
            o.addProperty("reason", r.reason);
            o.addProperty("timestamp", r.timestamp);
            o.addProperty("status", r.status);
            o.addProperty("target", r.target);
            return o;
        }
    }

    private static class StatsHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            try {
                if (!rateLimit(exchange)) return;
                String auth = exchange.getRequestHeaders().getFirst("Authorization");
                if (!AuthManager.checkBearer(auth) || !AuthManager.checkIp(exchange.getRemoteAddress())) {
                    writeJson(exchange, 401, AuthManager.errorJson("Unauthorized"));
                    return;
                }
                int totalPlayers = ReportManager.getReports().size();
                int totalReports = ReportManager.getReports().values().stream().mapToInt(List::size).sum();
                JsonObject o = new JsonObject();
                o.addProperty("players", totalPlayers);
                o.addProperty("reports", totalReports);
                writeJson(exchange, 200, o);
            } catch (Exception e) {
                ErrorManager.logError("REST_STATS", e);
                writeJson(exchange, 500, AuthManager.errorJson("Internal error"));
            }
        }
    }

    private static JsonObject message(String s) { JsonObject o = new JsonObject(); o.addProperty("message", s); return o; }

    private static boolean rateLimit(HttpExchange ex) throws IOException {
        String ip = ex.getRemoteAddress() != null && ex.getRemoteAddress().getAddress() != null ? ex.getRemoteAddress().getAddress().getHostAddress() : "unknown";
        RateBucket b = buckets.computeIfAbsent(ip, k -> new RateBucket());
        if (!b.allow(RATE_LIMIT_RPS)) {
            writeJson(ex, 429, AuthManager.errorJson("Too Many Requests"));
            return false;
        }
        return true;
    }

    private static void writeJson(HttpExchange exchange, int code, Object body) throws IOException {
        byte[] bytes = gson.toJson(body).getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(code, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static String readBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static long parseLong(String s, long def) {
        try { return Long.parseLong(s); } catch (Exception e) { return def; }
    }

    private static String urlDecode(String s) {
        try { return URLDecoder.decode(s, StandardCharsets.UTF_8); } catch (Exception e) { return s; }
    }

    private static JsonObject safeParse(String body) {
        try { return gson.fromJson(body, JsonObject.class); } catch (Exception e) { return null; }
    }

    private static class RateBucket {
        long windowStart = System.currentTimeMillis();
        int count = 0;
        synchronized boolean allow(int limit) {
            long now = System.currentTimeMillis();
            if (now - windowStart >= 1000) {
                windowStart = now;
                count = 0;
            }
            if (count >= limit) return false;
            count++;
            return true;
        }
    }
}
