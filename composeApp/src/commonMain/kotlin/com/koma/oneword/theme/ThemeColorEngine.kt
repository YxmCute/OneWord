package com.koma.oneword.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import com.koma.oneword.model.ThemeMode
import kotlin.math.max
import kotlin.math.min

object ThemeDefaults {
    const val DefaultSeedHex = "#0F5A46"
    val Presets = listOf(
        "#0F5A46",
        "#234E52",
        "#8C4A2F",
        "#6A3F7B",
        "#2E4057",
        "#8A5A44",
        "#506D4E",
        "#B05454",
    )
}

data class AppColorScheme(
    val backgroundTop: Color,
    val backgroundBottom: Color,
    val blobColors: List<Color>,
    val paperCard: Color,
    val paperShadow: Color,
    val primaryText: Color,
    val secondaryText: Color,
    val accent: Color,
    val accentMuted: Color,
    val outline: Color,
    val buttonBackground: Color,
    val buttonContent: Color,
)

object ThemeColorEngine {
    fun deriveScheme(seedHex: String, mode: ThemeMode, isSystemDark: Boolean): AppColorScheme {
        val resolvedMode = when (mode) {
            ThemeMode.SYSTEM -> if (isSystemDark) ThemeMode.DARK else ThemeMode.LIGHT
            else -> mode
        }
        val seed = seedHex.toColorOrDefault()
        val hue = seed.toHsv().first
        val darkFactor = if (resolvedMode == ThemeMode.DARK) 1f else 0.82f

        val backgroundTop = Color.hsv(hue, 0.58f, 0.20f * darkFactor)
        val backgroundBottom = Color.hsv((hue + 22f) % 360f, 0.64f, 0.14f * darkFactor)
        val accent = Color.hsv(hue, 0.72f, clamp(seed.toHsv().third * 0.95f, 0.42f, 0.76f))
        val buttonBackground = lerp(accent, Color.White, 0.82f)
        val paperCard = lerp(Color(0xFFF7F2E9), accent, 0.05f)
        val primaryText = ensureReadableText(base = accent, background = paperCard)
        val secondaryText = lerp(primaryText, paperCard, 0.42f)
        val outline = lerp(primaryText, paperCard, 0.72f)
        val blobColors = listOf(
            accent.copy(alpha = 0.50f),
            Color.hsv((hue + 28f) % 360f, 0.55f, 0.70f).copy(alpha = 0.36f),
            Color.hsv((hue + 330f) % 360f, 0.48f, 0.64f).copy(alpha = 0.26f),
        )

        return AppColorScheme(
            backgroundTop = backgroundTop,
            backgroundBottom = backgroundBottom,
            blobColors = blobColors,
            paperCard = paperCard,
            paperShadow = Color.Black.copy(alpha = 0.18f),
            primaryText = primaryText,
            secondaryText = secondaryText,
            accent = accent,
            accentMuted = accent.copy(alpha = 0.18f),
            outline = outline,
            buttonBackground = buttonBackground,
            buttonContent = ensureReadableText(base = accent, background = buttonBackground),
        )
    }

    private fun ensureReadableText(base: Color, background: Color): Color {
        val darkCandidate = lerp(base, Color.Black, 0.45f)
        val lightCandidate = lerp(base, Color.White, 0.80f)
        return if (contrastRatio(darkCandidate, background) >= contrastRatio(lightCandidate, background)) {
            darkCandidate
        } else {
            lightCandidate
        }
    }

    private fun contrastRatio(foreground: Color, background: Color): Float {
        val lighter = max(foreground.luminance(), background.luminance()) + 0.05f
        val darker = min(foreground.luminance(), background.luminance()) + 0.05f
        return lighter / darker
    }

    private fun clamp(value: Float, min: Float, max: Float): Float = max(min, min(value, max))
}
