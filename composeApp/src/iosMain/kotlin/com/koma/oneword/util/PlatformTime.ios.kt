package com.koma.oneword.util

/**
 * iOS time helpers for formatting timestamps shown in shared UI.
 */

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.timeIntervalSince1970

actual object PlatformTime {
    private val formatter = NSDateFormatter().apply {
        dateFormat = "MM-dd HH:mm"
    }

    actual fun currentTimeMillis(): Long = (NSDate().timeIntervalSince1970 * 1000.0).toLong()

    actual fun formatTimestamp(epochMillis: Long): String = formatter.stringFromDate(
        NSDate(timeIntervalSinceReferenceDate = epochMillis.toDouble() / 1000.0 - 978307200.0),
    )
}
