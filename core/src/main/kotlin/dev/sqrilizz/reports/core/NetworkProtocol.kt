package dev.sqrilizz.reports.core

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.util.UUID

object NetworkProtocol {
    const val CHANNEL = "sqrilizzreports:main"
    private const val TRANSFER = 1
    private const val OPEN_REPORT = 2

    data class TransferRequest(val moderatorId: UUID, val serverId: String, val reportId: Long)
    data class OpenReport(val moderatorId: UUID, val reportId: Long)

    fun encodeTransfer(value: TransferRequest): ByteArray = encode(TRANSFER) { output ->
        output.writeUTF(value.moderatorId.toString())
        output.writeUTF(value.serverId)
        output.writeLong(value.reportId)
    }

    fun encodeOpen(value: OpenReport): ByteArray = encode(OPEN_REPORT) { output ->
        output.writeUTF(value.moderatorId.toString())
        output.writeLong(value.reportId)
    }

    fun decode(bytes: ByteArray): Any? = runCatching {
        DataInputStream(ByteArrayInputStream(bytes)).use { input ->
            when (input.readUnsignedByte()) {
                TRANSFER -> TransferRequest(UUID.fromString(input.readUTF()), input.readUTF(), input.readLong())
                OPEN_REPORT -> OpenReport(UUID.fromString(input.readUTF()), input.readLong())
                else -> null
            }
        }
    }.getOrNull()

    private fun encode(type: Int, write: (DataOutputStream) -> Unit): ByteArray {
        val buffer = ByteArrayOutputStream()
        DataOutputStream(buffer).use {
            it.writeByte(type)
            write(it)
        }
        return buffer.toByteArray()
    }
}
