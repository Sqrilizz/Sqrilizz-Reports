package dev.sqrilizz.SQRILIZZREPORTS.api

import com.google.gson.JsonObject
import dev.sqrilizz.SQRILIZZREPORTS.Main
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object AuthManager {

    private var apiToken = ""
    private val ipWhitelist = mutableSetOf<String>()
    private var whitelistEnabled = false
    private var webhookSecret: String? = null

    @JvmStatic
    fun initialize() {
        val cfg = Main.getInstance().config
        apiToken = cfg.getString("rest-api.token", "") ?: ""
        whitelistEnabled = cfg.getBoolean("rest-api.ip-whitelist.enabled", false)
        ipWhitelist.clear()
        cfg.getStringList("rest-api.ip-whitelist.list")?.let { ipWhitelist.addAll(it) }
        webhookSecret = cfg.getString("rest-api.webhook.secret", null)
    }

    @JvmStatic
    fun checkBearer(headerAuth: String?): Boolean {
        if (apiToken.isEmpty() || headerAuth == null) return false
        if (!headerAuth.startsWith("Bearer ")) return false
        val token = headerAuth.substring("Bearer ".length).trim()
        return apiToken == token
    }

    @JvmStatic
    fun checkIp(remote: InetSocketAddress?): Boolean {
        if (!whitelistEnabled) return true
        if (remote == null) return false
        val host = remote.address?.hostAddress ?: remote.hostString
        return host in ipWhitelist
    }

    @JvmStatic
    fun verifyHmac(body: String?, signatureHeader: String?): Boolean {
        val secret = webhookSecret
        if (secret.isNullOrEmpty()) return true
        if (signatureHeader.isNullOrEmpty()) return false
        val safeBody = body ?: ""
        return try {
            val mac = Mac.getInstance("HmacSHA256")
            mac.init(SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256"))
            val raw = mac.doFinal(safeBody.toByteArray(StandardCharsets.UTF_8))
            val expected = Base64.getEncoder().encodeToString(raw)
            constantTimeEquals(expected, signatureHeader.trim())
        } catch (_: Exception) {
            false
        }
    }

    private fun constantTimeEquals(a: String?, b: String?): Boolean {
        if (a == null || b == null || a.length != b.length) return false
        var r = 0
        for (i in a.indices) {
            r = r or (a[i].code xor b[i].code)
        }
        return r == 0
    }

    @JvmStatic
    fun errorJson(message: String): JsonObject =
        JsonObject().apply { addProperty("error", message) }
}
