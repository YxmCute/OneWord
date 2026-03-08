package com.koma.oneword.util

/**
 * Expected time formatting API used by shared presentation logic.
 */

expect object PlatformTime {
    fun currentTimeMillis(): Long
    fun formatTimestamp(epochMillis: Long): String
}
