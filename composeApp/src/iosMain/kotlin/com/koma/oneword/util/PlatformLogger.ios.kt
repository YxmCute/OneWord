package com.koma.oneword.util

/**
 * iOS logger bridge used by shared Ktor and app diagnostics.
 */

actual object PlatformLogger {
    actual fun info(tag: String, message: String) {
        println("[$tag] $message")
    }

    actual fun error(tag: String, message: String) {
        println("[$tag][ERROR] $message")
    }
}
