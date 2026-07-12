package com.littlebridge.enrollplus.util

import kotlin.js.Date

actual fun currentTimeMillis(): Long = Date.now().toLong()

actual fun randomUUID(): String {
    val chars = "0123456789abcdef"
    val parts = CharArray(36) { '0' }
    for (i in 0 until 36) {
        when (i) {
            8, 13, 18, 23 -> parts[i] = '-'
            14 -> parts[i] = '4'
            else -> {
                val r = (Date.now() * 1000 + kotlin.random.Random.nextInt()).toInt() and 0xF
                parts[i] = if (i == 19) chars[(r and 0x3) or 0x8] else chars[r]
            }
        }
    }
    return parts.concatToString()
}
