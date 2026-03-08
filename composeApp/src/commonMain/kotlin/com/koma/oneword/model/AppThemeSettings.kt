package com.koma.oneword.model

data class AppThemeSettings(
    val mode: ThemeMode = ThemeMode.SYSTEM,
    val seedHex: String = "#0F5A46",
)
