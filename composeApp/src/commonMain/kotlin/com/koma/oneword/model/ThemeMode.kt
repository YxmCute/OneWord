package com.koma.oneword.model

/**
 * User-selectable theme modes with storage-safe parsing.
 */

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK;

    companion object {
        fun fromStorage(value: String?): ThemeMode = entries.firstOrNull { it.name == value } ?: SYSTEM
    }
}
