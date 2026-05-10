package dev.sqrilizz.SQRILIZZREPORTS

import java.io.File
import java.io.FileWriter
import java.net.URL
import java.net.URLClassLoader
import java.sql.Connection
import java.sql.Driver
import java.sql.DriverManager
import java.sql.SQLException

object MySQLOptionalManager {

    private var mysqlAvailable = false
    private var mysqlDriverPath: String? = null

    init {
        checkMySQLAvailability()
    }

    private fun checkMySQLAvailability() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver")
            mysqlAvailable = true
            Main.getInstance().logger.info("MySQL driver found in classpath")
        } catch (_: ClassNotFoundException) {
            val pluginFolder = Main.getInstance().dataFolder
            val libFolder = File(pluginFolder, "lib")

            if (libFolder.exists()) {
                val jarFiles = libFolder.listFiles { _, name ->
                    name.lowercase().contains("mysql") && name.endsWith(".jar")
                }

                if (!jarFiles.isNullOrEmpty()) {
                    try {
                        val jarUrl = jarFiles[0].toURI().toURL()
                        val classLoader = URLClassLoader(arrayOf(jarUrl))
                        val driverClass = classLoader.loadClass("com.mysql.cj.jdbc.Driver")
                        val driver = driverClass.getDeclaredConstructor().newInstance() as Driver
                        DriverManager.registerDriver(driver)

                        mysqlAvailable = true
                        mysqlDriverPath = jarFiles[0].absolutePath
                        Main.getInstance().logger.info("MySQL driver loaded from: $mysqlDriverPath")
                    } catch (ex: Exception) {
                        Main.getInstance().logger.warning("Failed to load external MySQL driver: ${ex.message}")
                    }
                }
            }

            if (!mysqlAvailable) {
                Main.getInstance().logger.info("MySQL driver not found. Plugin will use SQLite only.")
                Main.getInstance().logger.info("To enable MySQL support, place mysql-connector-j.jar in plugins/Sqrilizz-Reports/lib/ folder")
            }
        }
    }

    @JvmStatic
    fun isMySQLAvailable(): Boolean = mysqlAvailable

    @JvmStatic
    @Throws(SQLException::class)
    fun createMySQLConnection(url: String, username: String, password: String): Connection {
        if (!mysqlAvailable) {
            throw SQLException("MySQL driver is not available")
        }
        return DriverManager.getConnection(url, username, password)
    }

    @JvmStatic
    fun getMySQLDriverInfo(): String = when {
        !mysqlAvailable -> "MySQL driver not available"
        mysqlDriverPath != null -> "External MySQL driver: $mysqlDriverPath"
        else -> "Built-in MySQL driver"
    }

    @JvmStatic
    fun setupMySQLInstructions() {
        val pluginFolder = Main.getInstance().dataFolder
        val libFolder = File(pluginFolder, "lib")

        if (!libFolder.exists()) {
            libFolder.mkdirs()

            val readmeFile = File(libFolder, "README.txt")
            try {
                FileWriter(readmeFile).use { writer ->
                    writer.write("""
                        |MySQL Support Setup Instructions
                        |================================
                        |
                        |To enable MySQL support for Sqrilizz-Reports:
                        |
                        |1. Download mysql-connector-j.jar from:
                        |   https://dev.mysql.com/downloads/connector/j/
                        |
                        |2. Place the JAR file in this folder (plugins/Sqrilizz-Reports/lib/)
                        |
                        |3. Restart the server
                        |
                        |The plugin will automatically detect and load the MySQL driver.
                        |If MySQL driver is not found, the plugin will use SQLite instead.
                    """.trimMargin())
                }
            } catch (_: Exception) {
                // ignore
            }
        }
    }
}
