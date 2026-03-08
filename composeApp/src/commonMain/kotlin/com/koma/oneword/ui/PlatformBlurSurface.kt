package com.koma.oneword.ui

/**
 * Expected API for platform-specific blur layers behind the shared poster background.
 */

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

enum class BlurStyle {
    LIGHT,
    DARK,
}

enum class BlurStrength(val radiusDp: Float) {
    SOFT(42f),
    MEDIUM(80f),
    STRONG(112f),
}

expect object PlatformBlurSurface {
    @Composable
    fun BackgroundBlur(
        modifier: Modifier = Modifier,
        style: BlurStyle = BlurStyle.DARK,
        strength: BlurStrength = BlurStrength.MEDIUM,
    )
}
