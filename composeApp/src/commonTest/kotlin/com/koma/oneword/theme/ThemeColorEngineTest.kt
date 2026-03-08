package com.koma.oneword.theme

/**
 * Tests that the generated color scheme stays readable across light and dark inputs.
 */

import androidx.compose.ui.graphics.luminance
import com.koma.oneword.model.ThemeMode
import kotlin.test.Test
import kotlin.test.assertTrue

class ThemeColorEngineTest {
    @Test
    fun `derived scheme keeps text readable on paper card`() {
        val scheme = ThemeColorEngine.deriveScheme(
            seedHex = "#F7F1DE",
            mode = ThemeMode.LIGHT,
            isSystemDark = false,
        )

        assertTrue(scheme.primaryText.luminance() < scheme.paperCard.luminance())
        assertTrue(scheme.buttonContent.luminance() < scheme.buttonBackground.luminance())
    }

    @Test
    fun `dark mode keeps deep background`() {
        val scheme = ThemeColorEngine.deriveScheme(
            seedHex = "#0F5A46",
            mode = ThemeMode.DARK,
            isSystemDark = true,
        )

        assertTrue(scheme.backgroundTop.luminance() < 0.12f)
        assertTrue(scheme.backgroundBottom.luminance() < 0.10f)
    }
}
