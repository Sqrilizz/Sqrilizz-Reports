package dev.sqrilizz.SQRILIZZREPORTS.errors

import com.google.gson.GsonBuilder
import dev.sqrilizz.SQRILIZZREPORTS.Main
import java.io.File
import java.io.FileWriter
import java.io.IOException

object ErrorManager {

    private val gson = GsonBuilder().setPrettyPrinting().create()

    @JvmStatic
    fun logError(code: String, t: Throwable) {
        Main.getInstance().logger.severe("[$code] ${t.message}")
        t.printStackTrace()
        writeBackup("error", code, t.message)
    }

    @JvmStatic
    fun writeBackup(type: String, key: String, payload: Any?) {
        try {
            val dataFolder = Main.getInstance().dataFolder
            if (!dataFolder.exists()) dataFolder.mkdirs()

            val backup = File(dataFolder, "reports_backup.json")
            val obj = mapOf(
                "timestamp" to System.currentTimeMillis(),
                "type" to type,
                "key" to key,
                "payload" to payload
            )

            FileWriter(backup, true).use { fw ->
                fw.write(gson.toJson(obj))
                fw.write("\n")
            }
        } catch (_: IOException) {
        }
    }
}
