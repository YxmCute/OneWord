package com.koma.oneword.presentation

import com.koma.oneword.data.repository.SettingsRepository
import com.koma.oneword.model.AppThemeSettings
import com.koma.oneword.model.ThemeMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val scope: CoroutineScope,
) {
    val uiState: StateFlow<AppThemeSettings> = settingsRepository.observeTheme().stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = settingsRepository.currentTheme(),
    )

    fun updateThemeMode(mode: ThemeMode) {
        scope.launch {
            settingsRepository.saveThemeMode(mode)
        }
    }
}
