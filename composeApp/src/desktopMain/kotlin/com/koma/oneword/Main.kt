package com.koma.oneword

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import com.koma.oneword.app.AppContainer
import com.koma.oneword.app.AppContainerFactory
import com.koma.oneword.presentation.HomeViewModel
import com.koma.oneword.presentation.SettingsViewModel
import com.koma.oneword.theme.OneWordTheme
import com.koma.oneword.ui.components.ThemePickerOverlay
import com.koma.oneword.ui.home.HomeScreen
import com.koma.oneword.ui.settings.SettingsScreen

private enum class DesktopScreen {
    HOME,
    SETTINGS,
}

fun main() = application {
    val windowState = WindowState(
        size = DpSize(1280.dp, 900.dp),
        position = WindowPosition.Aligned(Alignment.Center),
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = "OneWord",
        state = windowState,
        icon = painterResource("app-icon.png"),
    ) {
        DesktopApp(
            onQuit = ::exitApplication,
        )
    }
}

@Composable
private fun FrameWindowScope.DesktopApp(
    onQuit: () -> Unit,
    container: AppContainer = AppContainerFactory.rememberAppContainer(),
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
    var screen by remember { mutableStateOf(DesktopScreen.HOME) }
    var previewSeedHex by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        homeViewModel.load()
    }

    MenuBar {
        Menu("OneWord") {
            Item(
                text = "首页",
                onClick = { screen = DesktopScreen.HOME },
                shortcut = KeyShortcut(Key.Home),
            )
            Item(
                text = "设置",
                onClick = { screen = DesktopScreen.SETTINGS },
                shortcut = KeyShortcut(Key.Comma, meta = true),
            )
            Item(
                text = "主题取色",
                onClick = homeViewModel::openThemePicker,
                shortcut = KeyShortcut(Key.T, meta = true, shift = true),
            )
            Separator()
            Item(
                text = "退出",
                onClick = onQuit,
                shortcut = KeyShortcut(Key.Q, meta = true),
            )
        }
        Menu("操作") {
            Item(
                text = "刷新诗句",
                onClick = {
                    screen = DesktopScreen.HOME
                    homeViewModel.refresh()
                },
                shortcut = KeyShortcut(Key.R, meta = true),
            )
            Item(
                text = if (homeState.showFullText) "收起全文" else "展开全文",
                onClick = {
                    screen = DesktopScreen.HOME
                    homeViewModel.toggleExpand()
                },
                shortcut = KeyShortcut(Key.Enter, meta = true),
            )
        }
    }

    OneWordTheme(
        settings = settingsState,
        previewSeedHex = previewSeedHex,
    ) {
        when (screen) {
            DesktopScreen.HOME -> HomeScreen(
                uiState = homeState,
                onRefresh = homeViewModel::refresh,
                onToggleExpand = homeViewModel::toggleExpand,
                onOpenThemePicker = homeViewModel::openThemePicker,
                onOpenSettings = { screen = DesktopScreen.SETTINGS },
                onDismissError = homeViewModel::dismissError,
            )
            DesktopScreen.SETTINGS -> SettingsScreen(
                settings = settingsState,
                onBack = { screen = DesktopScreen.HOME },
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
