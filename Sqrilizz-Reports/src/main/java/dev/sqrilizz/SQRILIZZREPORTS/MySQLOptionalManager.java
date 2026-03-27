package dev.sqrilizz.SQRILIZZREPORTS;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Optional MySQL manager that loads MySQL driver dynamically
 * This allows the plugin to work without MySQL JAR in the main plugin file
 * Users can put mysql-connector-j.jar in plugins/Sqrilizz-Reports/lib/ folder
 */
public class MySQLOptionalManager {
    private static boolean mysqlAvailable = false;
    private static String mysqlDriverPath = null;
    
    static {
        checkMySQLAvailability();
    }
    
    private static void checkMySQLAvailability() {
        try {
            // Try to load MySQL driver from classpath first
            Class.forName("com.mysql.cj.jdbc.Driver");
            mysqlAvailable = true;
            Main.getInstance().getLogger().info("MySQL driver found in classpath");
        } catch (ClassNotFoundException e) {
            // Try to find MySQL driver in plugins folder
            java.io.File pluginFolder = Main.getInstance().getDataFolder();
            java.io.File libFolder = new java.io.File(pluginFolder, "lib");
            
            if (libFolder.exists()) {
                java.io.File[] jarFiles = libFolder.listFiles((dir, name) -> 
                    name.toLowerCase().contains("mysql") && name.endsWith(".jar"));
                
                if (jarFiles != null && jarFiles.length > 0) {
                    try {
                        // Load MySQL driver from external JAR
                        java.net.URL jarUrl = jarFiles[0].toURI().toURL();
                        java.net.URLClassLoader classLoader = new java.net.URLClassLoader(new java.net.URL[]{jarUrl});
                        Class<?> driverClass = classLoader.loadClass("com.mysql.cj.jdbc.Driver");
                        Driver driver = (Driver) driverClass.getDeclaredConstructor().newInstance();
                        DriverManager.registerDriver(driver);
                        
                        mysqlAvailable = true;
                        mysqlDriverPath = jarFiles[0].getAbsolutePath();
                        Main.getInstance().getLogger().info("MySQL driver loaded from: " + mysqlDriverPath);
                    } catch (Exception ex) {
                        Main.getInstance().getLogger().warning("Failed to load external MySQL driver: " + ex.getMessage());
                    }
                }
            }
            
            if (!mysqlAvailable) {
                Main.getInstance().getLogger().info("MySQL driver not found. Plugin will use SQLite only.");
                Main.getInstance().getLogger().info("To enable MySQL support, place mysql-connector-j.jar in plugins/Sqrilizz-Reports/lib/ folder");
            }
        }
    }
    
    public static boolean isMySQLAvailable() {
        return mysqlAvailable;
    }
    
    public static Connection createMySQLConnection(String url, String username, String password) throws SQLException {
        if (!mysqlAvailable) {
            throw new SQLException("MySQL driver is not available");
        }
        
        return DriverManager.getConnection(url, username, password);
    }
    
    public static String getMySQLDriverInfo() {
        if (!mysqlAvailable) {
            return "MySQL driver not available";
        }
        
        if (mysqlDriverPath != null) {
            return "External MySQL driver: " + mysqlDriverPath;
        } else {
            return "Built-in MySQL driver";
        }
    }
    
    /**
     * Creates lib folder and provides instructions for MySQL setup
     */
    public static void setupMySQLInstructions() {
        java.io.File pluginFolder = Main.getInstance().getDataFolder();
        java.io.File libFolder = new java.io.File(pluginFolder, "lib");
        
        if (!libFolder.exists()) {
            libFolder.mkdirs();
            
            // Create README file with instructions
            java.io.File readmeFile = new java.io.File(libFolder, "README.txt");
            try (java.io.FileWriter writer = new java.io.FileWriter(readmeFile)) {
                writer.write("MySQL Support Setup Instructions\n");
                writer.write("================================\n\n");
                writer.write("To enable MySQL support for Sqrilizz-Reports:\n\n");
                writer.write("1. Download mysql-connector-j.jar from:\n");
                writer.write("   https://dev.mysql.com/downloads/connector/j/\n\n");
                writer.write("2. Place the JAR file in this folder (plugins/Sqrilizz-Reports/lib/)\n\n");
                writer.write("3. Restart the server\n\n");
                writer.write("The plugin will automatically detect and load the MySQL driver.\n");
                writer.write("If MySQL driver is not found, the plugin will use SQLite instead.\n");
            } catch (Exception e) {
                Main.getInstance().getLogger().warning("Could not create MySQL setup instructions: " + e.getMessage());
            }
        }
    }
}
