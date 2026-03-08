package com.koma.oneword.model

/**
 * Persisted theme settings shared across all targets.
 */

data class AppThemeSettings(
    val mode: ThemeMode = ThemeMode.SYSTEM,
    val seedHex: String = "#0F5A46",
)
