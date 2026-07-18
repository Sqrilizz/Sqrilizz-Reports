package dev.sqrilizz.SQRILIZZREPORTS;

import org.bukkit.configuration.file.FileConfiguration;

public final class ConfigMigration {
    private ConfigMigration() {}

    public static void apply() {
        FileConfiguration config = Main.getInstance().getConfig();
        boolean changed = false;
        changed |= move(config, "anonymous-reports", "reports.anonymous");
        changed |= move(config, "cooldown", "reports.cooldown");
        changed |= move(config, "report-limits.per-player", "anti-abuse.per-player-limit");
        changed |= move(config, "report-limits.per-hour", "anti-abuse.hourly-limit");
        changed |= move(config, "design.hex-colors", "design.use-hex-colors");
        changed |= move(config, "database.mysql.username", "database.mysql.user");
        changed |= move(config, "rest-api.rate-limit.requests-per-second", "rest-api.rate-limit.rps");
        changed |= move(config, "rest-api.ip-whitelist.ips", "rest-api.ip-whitelist.list");
        changed |= move(config, "rest-api.webhook-secret", "rest-api.webhook.secret");
        if (changed) {
            Main.getInstance().saveConfig();
            Main.getInstance().getLogger().info("Migrated legacy configuration keys");
        }
    }

    private static boolean move(FileConfiguration config, String legacy, String current) {
        if (!config.contains(legacy) || config.contains(current)) return false;
        config.set(current, config.get(legacy));
        return true;
    }
}
