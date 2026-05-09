package dev.sqrilizz.SQRILIZZREPORTS.db;

import dev.sqrilizz.SQRILIZZREPORTS.Main;
import dev.sqrilizz.SQRILIZZREPORTS.ReportManager;
import java.util.*;

public class DatabaseManager {
    private static Driver driver;

    public static void initialize() {
        var cfg = Main.getInstance().getConfig();
        String type = cfg.getString("database.type", "json").toLowerCase(Locale.ROOT);
        
        if ("mysql".equals(type)) {
            driver = new MySQLDriver();
            Main.getInstance().getLogger().info("Using MySQL database storage");
        } else if ("sqlite".equals(type)) {
            driver = new SQLiteDriver();
            Main.getInstance().getLogger().info("Using SQLite database storage");
        } else {
            driver = new JsonDriver();
            Main.getInstance().getLogger().info("Using JSON file storage (lightweight)");
        }
        
        driver.init();
    }

    public static long saveReport(ReportManager.Report r) {
        return driver.saveReport(r);
    }

    public static boolean resolveReport(long id, String resolver) {
        return driver.resolveReport(id, resolver);
    }

    public static boolean addReply(long reportId, String author, String message, long ts) {
        return driver.addReply(reportId, author, message, ts);
    }

    public static Map<String, List<ReportManager.Report>> loadReports() {
        return driver.loadReports();
    }

    public static List<ReportManager.Report> getReportsByPlayer(String player) {
        return driver.getReportsByPlayer(player);
    }

    public static void replaceAllReports(Map<String, List<ReportManager.Report>> reports) {
        driver.replaceAllReports(reports);
    }

    public static void close() {
        try { driver.close(); } catch (Exception ignored) {}
    }

    interface Driver {
        void init();
        long saveReport(ReportManager.Report r);
        boolean resolveReport(long id, String resolver);
        boolean addReply(long reportId, String author, String message, long ts);
        Map<String, List<ReportManager.Report>> loadReports();
        List<ReportManager.Report> getReportsByPlayer(String player);
        void replaceAllReports(Map<String, List<ReportManager.Report>> reports);
        void close();
    }
}
