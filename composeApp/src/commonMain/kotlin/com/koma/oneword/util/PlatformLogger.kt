package com.koma.oneword.util

expect object PlatformLogger {
    fun info(tag: String, message: String)
    fun error(tag: String, message: String)
}
