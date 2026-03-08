package com.koma.oneword.theme

import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

fun String.toColorOrDefault(default: Color = Color(0xFF0F5A46)): Color {
    val normalized = trim().removePrefix("#")
    val value = normalized.toLongOrNull(16) ?: return default
    return when (normalized.length) {
        6 -> Color(
            red = ((value shr 16) and 0xFF).toInt() / 255f,
            green = ((value shr 8) and 0xFF).toInt() / 255f,
            blue = (value and 0xFF).toInt() / 255f,
            alpha = 1f,
        )
        8 -> Color(
            red = ((value shr 16) and 0xFF).toInt() / 255f,
            green = ((value shr 8) and 0xFF).toInt() / 255f,
            blue = (value and 0xFF).toInt() / 255f,
            alpha = ((value shr 24) and 0xFF).toInt() / 255f,
        )
        else -> default
    }
}

fun Color.toHex(): String {
    val red = (red * 255).toInt().coerceIn(0, 255)
    val green = (green * 255).toInt().coerceIn(0, 255)
    val blue = (blue * 255).toInt().coerceIn(0, 255)
    return buildString {
        append('#')
        append(red.toHexByte())
        append(green.toHexByte())
        append(blue.toHexByte())
    }
}

fun Color.toHsv(): Triple<Float, Float, Float> {
    val r = red
    val g = green
    val b = blue
    val max = max(max(r, g), b)
    val min = min(min(r, g), b)
    val delta = max - min

    val hue = when {
        delta == 0f -> 0f
        max == r -> 60f * (((g - b) / delta) % 6f)
        max == g -> 60f * (((b - r) / delta) + 2f)
        else -> 60f * (((r - g) / delta) + 4f)
    }.let { if (it < 0f) it + 360f else it }

    val saturation = if (max == 0f) 0f else delta / max
    val value = max
    return Triple(hue, saturation, value)
}

fun Color.withBrightness(value: Float): Color {
    val hsv = toHsv()
    return Color.hsv(hsv.first, hsv.second, value.coerceIn(0f, 1f))
}

fun Color.withSaturation(value: Float): Color {
    val hsv = toHsv()
    return Color.hsv(hsv.first, value.coerceIn(0f, 1f), hsv.third)
}

fun colorFromHsv(hue: Float, saturation: Float, value: Float): Color = Color.hsv(
    ((hue % 360f) + 360f) % 360f,
    saturation.coerceIn(0f, 1f),
    value.coerceIn(0f, 1f),
)

private fun Int.toHexByte(): String = toString(16).padStart(2, '0').uppercase()
