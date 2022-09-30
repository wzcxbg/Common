package com.sliver.common.util

object BytesUtil {
    fun toHexString(buffer: ByteArray, upperCase: Boolean = true): String {
        val sb = StringBuilder()
        for (byte in buffer) {
            sb.append(Integer.toHexString(byte.toInt()))
        }
        return if (upperCase) sb.toString().uppercase() else sb.toString()
    }


    fun fromHexString(hexStr: String): ByteArray {
        val chars = hexStr.uppercase().toCharArray()
        val digits = "0123456789ABCDEF"
        val bufferSize = hexStr.length / 2
        val buffer = ByteArray(bufferSize)
        for (i in 0 until bufferSize) {
            val high = digits.indexOf(chars[i * 2 + 0])
            val low = digits.indexOf(chars[i * 2 + 1])
            buffer[i] = (high.shl(4).and(low)).toByte()
        }
        return buffer
    }
}