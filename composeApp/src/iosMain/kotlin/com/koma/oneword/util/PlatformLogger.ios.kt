package com.koma.oneword.util

actual object PlatformLogger {
    actual fun info(tag: String, message: String) {
        println("[$tag] $message")
    }

    actual fun error(tag: String, message: String) {
        println("[$tag][ERROR] $message")
    }
}
