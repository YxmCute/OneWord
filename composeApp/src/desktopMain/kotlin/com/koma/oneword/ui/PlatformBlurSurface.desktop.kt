package com.koma.oneword.ui

/**
 * Desktop blur overlay implementation for the poster background.
 */

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

actual object PlatformBlurSurface {
    @Composable
    actual fun BackgroundBlur(
        modifier: Modifier,
        style: BlurStyle,
        strength: BlurStrength,
    ) {
        val tint = if (style == BlurStyle.DARK) Color.White.copy(alpha = 0.06f) else Color.White.copy(alpha = 0.10f)
        Box(modifier = modifier) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(tint, Color.Transparent),
                        ),
                    )
                    .blur(strength.radiusDp.dp),
            )
        }
    }
}
