package com.koma.oneword.util

expect object PlatformTime {
    fun currentTimeMillis(): Long
    fun formatTimestamp(epochMillis: Long): String
}
