package com.koma.oneword.util

/**
 * Desktop/JVM time helpers for UI timestamps.
 */

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

actual object PlatformTime {
    private val formatter = DateTimeFormatter.ofPattern("MM-dd HH:mm")

    actual fun currentTimeMillis(): Long = System.currentTimeMillis()

    actual fun formatTimestamp(epochMillis: Long): String = formatter.format(
        Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault()),
    )
}
