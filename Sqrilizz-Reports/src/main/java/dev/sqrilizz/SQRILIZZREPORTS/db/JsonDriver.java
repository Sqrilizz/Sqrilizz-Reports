package dev.sqrilizz.SQRILIZZREPORTS.db;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import dev.sqrilizz.SQRILIZZREPORTS.Main;
import dev.sqrilizz.SQRILIZZREPORTS.ReportManager;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * JSON-based storage driver for reports
 * Lightweight alternative to SQLite with no native dependencies
 */
public class JsonDriver implements DatabaseManager.Driver {
    
    private final File dataFile;
    private final File backupFile;
    private final Gson gson;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<String, List<ReportManager.Report>> cache = new ConcurrentHashMap<>();
    private long lastSaveTime = 0;
    private static final long SAVE_INTERVAL = 30000; // 30 seconds
    
    public JsonDriver() {
        File dataFolder = Main.getInstance().getDataFolder();
        this.dataFile = new File(dataFolder, "reports.json");
        this.backupFile = new File(dataFolder, "reports.json.backup");
        this.gson = new GsonBuilder()
            .setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .create();
    }
    
    @Override
    public void init() {
        try {
            // Create data folder if not exists
            if (!Main.getInstance().getDataFolder().exists()) {
                Main.getInstance().getDataFolder().mkdirs();
            }
            
            // Load existing data
            if (dataFile.exists()) {
                loadFromFile();
                Main.getInstance().getLogger().info("Loaded reports from JSON storage");
            } else {
                Main.getInstance().getLogger().info("Created new JSON storage");
            }
            
            // Start auto-save task
            startAutoSaveTask();
            
        } catch (Exception e) {
            Main.getInstance().getLogger().severe("Failed to initialize JSON storage: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public long saveReport(ReportManager.Report report) {
        lock.writeLock().lock();
        try {
            // Generate ID if new report
            if (report.id == 0) {
                report.id = generateNextId();
            }
            
            // Add to cache
            cache.computeIfAbsent(report.target, k -> new ArrayList<>()).add(report);
            
            // Save to disk (throttled)
            saveToFileThrottled();
            
            return report.id;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public boolean resolveReport(long id, String resolver) {
        lock.writeLock().lock();
        try {
            for (List<ReportManager.Report> reports : cache.values()) {
                for (ReportManager.Report report : reports) {
                    if (report.id == id) {
                        report.status = "resolved";
                        // Note: resolver and resolvedAt are not stored in current Report structure
                        saveToFileThrottled();
                        return true;
                    }
                }
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public boolean addReply(long reportId, String author, String message, long timestamp) {
        lock.writeLock().lock();
        try {
            for (List<ReportManager.Report> reports : cache.values()) {
                for (ReportManager.Report report : reports) {
                    if (report.id == reportId) {
                        // Generate reply ID
                        long replyId = System.currentTimeMillis();
                        ReportManager.Reply reply = new ReportManager.Reply(replyId, reportId, author, message, timestamp);
                        report.replies.add(reply);
                        saveToFileThrottled();
                        return true;
                    }
                }
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public Map<String, List<ReportManager.Report>> loadReports() {
        lock.readLock().lock();
        try {
            // Return a copy to prevent external modifications
            Map<String, List<ReportManager.Report>> result = new HashMap<>();
            for (Map.Entry<String, List<ReportManager.Report>> entry : cache.entrySet()) {
                result.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
            return result;
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public List<ReportManager.Report> getReportsByPlayer(String player) {
        lock.readLock().lock();
        try {
            List<ReportManager.Report> reports = cache.get(player);
            return reports != null ? new ArrayList<>(reports) : new ArrayList<>();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public void replaceAllReports(Map<String, List<ReportManager.Report>> reports) {
        lock.writeLock().lock();
        try {
            cache.clear();
            cache.putAll(reports);
            saveToFileImmediate();
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public void close() {
        // Force save on close
        saveToFileImmediate();
        Main.getInstance().getLogger().info("JSON storage closed and saved");
    }
    
    /**
     * Load reports from JSON file
     */
    private void loadFromFile() {
        try (Reader reader = new InputStreamReader(new FileInputStream(dataFile), StandardCharsets.UTF_8)) {
            Type type = new TypeToken<Map<String, List<ReportManager.Report>>>(){}.getType();
            Map<String, List<ReportManager.Report>> loaded = gson.fromJson(reader, type);
            
            if (loaded != null) {
                cache.clear();
                cache.putAll(loaded);
            }
        } catch (Exception e) {
            Main.getInstance().getLogger().warning("Failed to load reports from JSON, trying backup...");
            
            // Try to load from backup
            if (backupFile.exists()) {
                try (Reader reader = new InputStreamReader(new FileInputStream(backupFile), StandardCharsets.UTF_8)) {
                    Type type = new TypeToken<Map<String, List<ReportManager.Report>>>(){}.getType();
                    Map<String, List<ReportManager.Report>> loaded = gson.fromJson(reader, type);
                    
                    if (loaded != null) {
                        cache.clear();
                        cache.putAll(loaded);
                        Main.getInstance().getLogger().info("Loaded reports from backup");
                    }
                } catch (Exception e2) {
                    Main.getInstance().getLogger().severe("Failed to load backup: " + e2.getMessage());
                }
            }
        }
    }
    
    /**
     * Save to file with throttling (max once per SAVE_INTERVAL)
     */
    private void saveToFileThrottled() {
        long now = System.currentTimeMillis();
        if (now - lastSaveTime > SAVE_INTERVAL) {
            saveToFileImmediate();
        }
    }
    
    /**
     * Save to file immediately
     */
    private void saveToFileImmediate() {
        try {
            // Create backup of existing file
            if (dataFile.exists()) {
                Files.copy(dataFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            
            // Write to temp file first
            File tempFile = new File(dataFile.getParentFile(), "reports.json.tmp");
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(tempFile), StandardCharsets.UTF_8)) {
                gson.toJson(cache, writer);
            }
            
            // Atomic move
            Files.move(tempFile.toPath(), dataFile.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            
            lastSaveTime = System.currentTimeMillis();
            
        } catch (Exception e) {
            Main.getInstance().getLogger().severe("Failed to save reports to JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Generate next available ID
     */
    private long generateNextId() {
        long maxId = 0;
        for (List<ReportManager.Report> reports : cache.values()) {
            for (ReportManager.Report report : reports) {
                if (report.id > maxId) {
                    maxId = report.id;
                }
            }
        }
        return maxId + 1;
    }
    
    /**
     * Start auto-save task
     */
    private void startAutoSaveTask() {
        Main.runTaskTimerAsync(() -> {
            if (System.currentTimeMillis() - lastSaveTime > SAVE_INTERVAL) {
                lock.writeLock().lock();
                try {
                    saveToFileImmediate();
                } finally {
                    lock.writeLock().unlock();
                }
            }
        }, 20L * 60, 20L * 60); // Every 60 seconds
    }
}
