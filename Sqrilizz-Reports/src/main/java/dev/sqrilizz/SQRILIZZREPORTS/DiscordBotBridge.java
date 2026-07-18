package dev.sqrilizz.SQRILIZZREPORTS;

import java.lang.reflect.Method;

public final class DiscordBotBridge {

    private static final String MANAGER_CLASS = "dev.sqrilizz.SQRILIZZREPORTS.DiscordBotManager";

    private DiscordBotBridge() {}

    public static void initialize() {
        invoke("initialize");
    }

    public static void reload() {
        invoke("reload");
    }

    public static void shutdown() {
        invoke("shutdown");
    }

    public static void sendReport(ReportManager.Report report, boolean bugReport) {
        invoke("sendReport", new Class<?>[] { ReportManager.Report.class, boolean.class }, report, bugReport);
    }

    private static void invoke(String methodName, Class<?>[] parameterTypes, Object... arguments) {
        try {
            Class<?> manager = Class.forName(MANAGER_CLASS);
            Method method = manager.getMethod(methodName, parameterTypes);
            method.invoke(null, arguments);
        } catch (ClassNotFoundException ignored) {
        } catch (ReflectiveOperationException exception) {
            Main.getInstance().getLogger().warning("Discord bot integration failed: " + exception.getMessage());
        }
    }

    private static void invoke(String methodName) {
        invoke(methodName, new Class<?>[0]);
    }
}
