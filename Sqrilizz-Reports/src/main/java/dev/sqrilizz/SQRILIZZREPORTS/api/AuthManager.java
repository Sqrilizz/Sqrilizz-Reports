package dev.sqrilizz.SQRILIZZREPORTS.api;

import com.google.gson.JsonObject;
import dev.sqrilizz.SQRILIZZREPORTS.Main;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * AuthManager handles Bearer token auth, optional IP whitelist, and optional HMAC signature validation
 */
public class AuthManager {
    private static String apiToken;
    private static Set<String> ipWhitelist = new HashSet<>();
    private static boolean whitelistEnabled = false;
    private static String webhookSecret = null;

    public static void initialize() {
        var cfg = Main.getInstance().getConfig();
        apiToken = cfg.getString("rest-api.token", "");
        whitelistEnabled = cfg.getBoolean("rest-api.ip-whitelist.enabled", false);
        List<String> wl = cfg.getStringList("rest-api.ip-whitelist.list");
        ipWhitelist.clear();
        if (wl != null) ipWhitelist.addAll(wl);
        webhookSecret = cfg.getString("rest-api.webhook.secret", null);
    }

    public static boolean checkBearer(String headerAuth) {
        if (apiToken == null || apiToken.isEmpty()) return false;
        if (headerAuth == null) return false;
        if (!headerAuth.startsWith("Bearer ")) return false;
        String token = headerAuth.substring("Bearer ".length()).trim();
        return apiToken.equals(token);
    }

    public static boolean checkIp(InetSocketAddress remote) {
        if (!whitelistEnabled) return true;
        if (remote == null) return false;
        String host = remote.getAddress() != null ? remote.getAddress().getHostAddress() : remote.getHostString();
        return ipWhitelist.contains(host);
    }

    public static boolean verifyHmac(String body, String signatureHeader) {
        if (webhookSecret == null || webhookSecret.isEmpty()) return true; // not required
        if (body == null) body = "";
        if (signatureHeader == null || signatureHeader.isEmpty()) return false;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] raw = mac.doFinal(body.getBytes(StandardCharsets.UTF_8));
            String expected = Base64.getEncoder().encodeToString(raw);
            return constantTimeEquals(expected, signatureHeader.trim());
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) return false;
        if (a.length() != b.length()) return false;
        int r = 0;
        for (int i = 0; i < a.length(); i++) {
            r |= a.charAt(i) ^ b.charAt(i);
        }
        return r == 0;
    }

    public static JsonObject errorJson(String message) {
        JsonObject o = new JsonObject();
        o.addProperty("error", message);
        return o;
    }
}
