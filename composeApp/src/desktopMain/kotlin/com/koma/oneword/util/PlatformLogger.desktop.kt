package com.koma.oneword.util

/**
 * Desktop logger bridge that sends diagnostics to stdout/stderr.
 */

actual object PlatformLogger {
    actual fun info(tag: String, message: String) {
        println("[$tag] $message")
    }

    actual fun error(tag: String, message: String) {
        System.err.println("[$tag] $message")
    }
}
