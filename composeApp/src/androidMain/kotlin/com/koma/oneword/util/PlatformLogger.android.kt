package com.koma.oneword.util

/**
 * Android logger bridge that routes structured app logs into logcat.
 */

import android.util.Log

actual object PlatformLogger {
    actual fun info(tag: String, message: String) {
        logByChunks(tag = tag, message = message, isError = false)
    }

    actual fun error(tag: String, message: String) {
        logByChunks(tag = tag, message = message, isError = true)
    }

    private fun logByChunks(tag: String, message: String, isError: Boolean) {
        val normalized = message.ifBlank { "<empty>" }
        normalized.lineSequence().forEach { line ->
            if (line.length <= MAX_LOG_LENGTH) {
                if (isError) Log.e(tag, line) else Log.i(tag, line)
            } else {
                line.chunked(MAX_LOG_LENGTH).forEach { chunk ->
                    if (isError) Log.e(tag, chunk) else Log.i(tag, chunk)
                }
            }
        }
    }

    private const val MAX_LOG_LENGTH = 3000
}
