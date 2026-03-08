package com.koma.oneword.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import com.koma.oneword.model.AppThemeSettings

@Immutable
data class OneWordThemeState(
    val settings: AppThemeSettings,
    val scheme: AppColorScheme,
)

val LocalOneWordTheme = compositionLocalOf<OneWordThemeState> {
    error("OneWordThemeState not provided")
}

@Composable
fun OneWordTheme(
    settings: AppThemeSettings,
    previewSeedHex: String? = null,
    content: @Composable () -> Unit,
) {
    val systemDark = isSystemInDarkTheme()
    val resolvedIsDark = when (settings.mode) {
        com.koma.oneword.model.ThemeMode.SYSTEM -> systemDark
        com.koma.oneword.model.ThemeMode.DARK -> true
        com.koma.oneword.model.ThemeMode.LIGHT -> false
    }
    val effectiveSettings = settings.copy(seedHex = previewSeedHex ?: settings.seedHex)
    val scheme = ThemeColorEngine.deriveScheme(
        seedHex = effectiveSettings.seedHex,
        mode = effectiveSettings.mode,
        isSystemDark = systemDark,
    )
    val material = scheme.toMaterialColorScheme(resolvedIsDark)

    CompositionLocalProvider(
        LocalOneWordTheme provides OneWordThemeState(effectiveSettings, scheme),
    ) {
        MaterialTheme(
            colorScheme = material,
            content = content,
        )
    }
}

private fun AppColorScheme.toMaterialColorScheme(isDark: Boolean): ColorScheme {
    return if (isDark) {
        darkColorScheme(
            primary = accent,
            onPrimary = buttonContent,
            secondary = accent,
            onSecondary = buttonContent,
            tertiary = outline,
            background = backgroundTop,
            onBackground = primaryText,
            surface = paperCard,
            onSurface = primaryText,
            surfaceVariant = accentMuted,
            onSurfaceVariant = secondaryText,
            outline = outline,
        )
    } else {
        lightColorScheme(
            primary = accent,
            onPrimary = buttonContent,
            secondary = accent,
            onSecondary = buttonContent,
            tertiary = outline,
            background = backgroundTop,
            onBackground = primaryText,
            surface = paperCard,
            onSurface = primaryText,
            surfaceVariant = accentMuted,
            onSurfaceVariant = secondaryText,
            outline = outline,
        )
    }
}
