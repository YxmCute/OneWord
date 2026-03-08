package com.koma.oneword

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import com.koma.oneword.app.AppContainer
import com.koma.oneword.app.AppContainerFactory
import com.koma.oneword.presentation.HomeViewModel
import com.koma.oneword.presentation.SettingsViewModel
import com.koma.oneword.theme.OneWordTheme
import com.koma.oneword.ui.AppInsetMode
import com.koma.oneword.ui.components.ThemePickerOverlay
import com.koma.oneword.ui.home.HomeScreen
import com.koma.oneword.ui.settings.SettingsScreen

private enum class Screen {
    HOME,
    SETTINGS,
}

@Composable
fun OneWordApp(
    container: AppContainer = AppContainerFactory.rememberAppContainer(),
    insetMode: AppInsetMode = AppInsetMode.SAFE_DRAWING,
) {
    DisposableEffect(container) {
        onDispose { container.close() }
    }

    val homeViewModel = remember(container) {
        HomeViewModel(
            poetryRepository = container.poetryRepository,
            settingsRepository = container.settingsRepository,
            scope = container.scope,
        )
    }
    val settingsViewModel = remember(container) {
        SettingsViewModel(
            settingsRepository = container.settingsRepository,
            scope = container.scope,
        )
    }

    val homeState by homeViewModel.uiState.collectAsState()
    val settingsState by settingsViewModel.uiState.collectAsState()
    var screen by remember { mutableStateOf(Screen.HOME) }
    var previewSeedHex by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        homeViewModel.load()
    }

    OneWordTheme(
        settings = settingsState,
        previewSeedHex = previewSeedHex,
    ) {
        when (screen) {
            Screen.HOME -> HomeScreen(
                uiState = homeState,
                insetMode = insetMode,
                onRefresh = homeViewModel::refresh,
                onToggleExpand = homeViewModel::toggleExpand,
                onOpenThemePicker = homeViewModel::openThemePicker,
                onOpenSettings = { screen = Screen.SETTINGS },
                onDismissError = homeViewModel::dismissError,
            )
            Screen.SETTINGS -> SettingsScreen(
                settings = settingsState,
                insetMode = insetMode,
                onBack = { screen = Screen.HOME },
                onUpdateMode = settingsViewModel::updateThemeMode,
                onOpenThemePicker = homeViewModel::openThemePicker,
            )
        }

        if (homeState.isThemePickerVisible) {
            ThemePickerOverlay(
                initialSeedHex = previewSeedHex ?: settingsState.seedHex,
                onPreview = { previewSeedHex = it },
                onDismiss = {
                    previewSeedHex = null
                    homeViewModel.closeThemePicker()
                },
                onConfirm = { hex ->
                    previewSeedHex = null
                    homeViewModel.updateThemeSeed(hex)
                },
            )
        }
    }
}
