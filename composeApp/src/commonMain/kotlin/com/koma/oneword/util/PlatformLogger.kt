package com.koma.oneword.util

/**
 * Expected logging API so shared code can emit platform-native diagnostics.
 */

expect object PlatformLogger {
    fun info(tag: String, message: String)
    fun error(tag: String, message: String)
}
