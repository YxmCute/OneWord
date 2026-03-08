package com.koma.oneword.ui

/**
 * Platform-aware safe-area policy for shared screens.
 */

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable

enum class AppInsetMode {
    SAFE_DRAWING,
    IMMERSIVE_TOP,
}

@Composable
fun AppInsetMode.contentInsets(): WindowInsets = when (this) {
    AppInsetMode.SAFE_DRAWING -> WindowInsets.safeDrawing
    AppInsetMode.IMMERSIVE_TOP -> WindowInsets.safeDrawing.only(
        WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom,
    )
}

@Composable
fun AppInsetMode.headerInsets(): WindowInsets = when (this) {
    AppInsetMode.SAFE_DRAWING -> WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
    AppInsetMode.IMMERSIVE_TOP -> WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
}
