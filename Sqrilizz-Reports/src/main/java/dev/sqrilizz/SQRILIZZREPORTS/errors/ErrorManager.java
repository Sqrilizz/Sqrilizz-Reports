package dev.sqrilizz.SQRILIZZREPORTS.errors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.sqrilizz.SQRILIZZREPORTS.Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ErrorManager {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void logError(String code, Throwable t) {
        Main.getInstance().getLogger().severe("[" + code + "] " + t.getMessage());
        t.printStackTrace();
        writeBackup("error", code, t.getMessage());
    }

    public static void writeBackup(String type, String key, Object payload) {
        try {
            File dataFolder = Main.getInstance().getDataFolder();
            if (!dataFolder.exists()) dataFolder.mkdirs();
            File backup = new File(dataFolder, "reports_backup.json");
            Map<String, Object> obj = new HashMap<>();
            obj.put("timestamp", System.currentTimeMillis());
            obj.put("type", type);
            obj.put("key", key);
            obj.put("payload", payload);
            try (FileWriter fw = new FileWriter(backup, true)) {
                fw.write(gson.toJson(obj));
                fw.write("\n");
            }
        } catch (IOException ignored) {
        }
    }
}
