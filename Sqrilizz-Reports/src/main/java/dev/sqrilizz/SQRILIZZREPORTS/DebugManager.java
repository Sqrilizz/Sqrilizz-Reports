package dev.sqrilizz.SQRILIZZREPORTS;

import java.util.logging.Logger;

public class DebugManager {
    private static boolean debugEnabled = false;
    private static Logger logger;

    public static void initialize() {
        logger = Main.getInstance().getLogger();
        debugEnabled = Main.getInstance().getConfig().getBoolean("debug.enabled", false);
        if (debugEnabled) {
            logger.info("[DEBUG] Debug mode is ENABLED");
        }
    }

    public static boolean isEnabled() {
        return debugEnabled;
    }

    public static void setEnabled(boolean enabled) {
        debugEnabled = enabled;
        Main.getInstance().getConfig().set("debug.enabled", enabled);
        Main.getInstance().saveConfig();
    }

    public static void toggle() {
        setEnabled(!debugEnabled);
    }

    public static void log(String message) {
        if (debugEnabled && logger != null) {
            logger.info("[DEBUG] " + message);
        }
    }

    public static void log(String category, String message) {
        if (debugEnabled && logger != null) {
            logger.info("[DEBUG][" + category + "] " + message);
        }
    }

    public static void warn(String message) {
        if (debugEnabled && logger != null) {
            logger.warning("[DEBUG] " + message);
        }
    }
}
