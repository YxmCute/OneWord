package com.koma.oneword.ui.components

/**
 * Shared decorative background with gradients, blobs, and the platform blur layer.
 */

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.koma.oneword.theme.AppColorScheme
import com.koma.oneword.ui.BlurStrength
import com.koma.oneword.ui.BlurStyle
import com.koma.oneword.ui.PlatformBlurSurface

@Composable
fun PosterBackground(
    scheme: AppColorScheme,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(scheme.backgroundTop, scheme.backgroundBottom),
                ),
            ),
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val width = maxWidth
            val height = maxHeight
            Blob(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(width * 0.72f)
                    .offset(x = (-96).dp, y = 48.dp),
                color = scheme.blobColors[0],
            )
            Blob(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(width * 0.66f)
                    .offset(x = 120.dp, y = (-40).dp),
                color = scheme.blobColors[1],
            )
            Blob(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(height * 0.48f)
                    .offset(y = 160.dp),
                color = scheme.blobColors[2],
            )
        }

        PlatformBlurSurface.BackgroundBlur(
            modifier = Modifier.fillMaxSize(),
            style = BlurStyle.DARK,
            strength = BlurStrength.STRONG,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.06f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.16f),
                        ),
                    ),
                ),
        )

        content()
    }
}

@Composable
private fun Blob(
    modifier: Modifier,
    color: Color,
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(color)
            .blur(118.dp),
    )
}
