package com.koma.oneword.presentation

import com.koma.oneword.data.repository.PoetryRepository
import com.koma.oneword.data.repository.SettingsRepository
import com.koma.oneword.model.HomeData
import com.koma.oneword.model.Poem
import com.koma.oneword.model.RefreshResult
import com.koma.oneword.util.PlatformTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val poem: Poem? = null,
    val showFullText: Boolean = false,
    val isOfflineContent: Boolean = false,
    val lastUpdatedText: String? = null,
    val error: String? = null,
    val themeSeedHex: String = "#0F5A46",
    val isThemePickerVisible: Boolean = false,
    val refreshAnimationKey: Long = 0L,
    val isPoemRevealRunning: Boolean = false,
)

class HomeViewModel(
    private val poetryRepository: PoetryRepository,
    private val settingsRepository: SettingsRepository,
    private val scope: CoroutineScope,
) {
    private val isLoading = MutableStateFlow(true)
    private val isRefreshing = MutableStateFlow(false)
    private val showFullText = MutableStateFlow(false)
    private val isOfflineContent = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)
    private val isThemePickerVisible = MutableStateFlow(false)
    private val refreshAnimationKey = MutableStateFlow(0L)
    private val isPoemRevealRunning = MutableStateFlow(false)
    private var hasLoaded = false
    private var revealJob: Job? = null

    val uiState: StateFlow<HomeUiState> = combine(
        poetryRepository.observeHome(),
        settingsRepository.observeTheme(),
        isLoading,
        isRefreshing,
        showFullText,
        isOfflineContent,
        errorMessage,
        isThemePickerVisible,
        refreshAnimationKey,
        isPoemRevealRunning,
    ) { values ->
        val home = values[0] as HomeData?
        val theme = values[1] as com.koma.oneword.model.AppThemeSettings
        HomeUiState(
            isLoading = values[2] as Boolean && home == null,
            isRefreshing = values[3] as Boolean,
            poem = home?.poem,
            showFullText = values[4] as Boolean,
            isOfflineContent = values[5] as Boolean,
            lastUpdatedText = home?.fetchedAtEpochMs?.let(PlatformTime::formatTimestamp),
            error = values[6] as String?,
            themeSeedHex = theme.seedHex,
            isThemePickerVisible = values[7] as Boolean,
            refreshAnimationKey = values[8] as Long,
            isPoemRevealRunning = values[9] as Boolean,
        )
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(),
    )

    fun load() {
        if (hasLoaded) return
        hasLoaded = true
        scope.launch {
            isLoading.value = true
            handleRefreshResult(poetryRepository.refresh(force = false))
            isLoading.value = false
        }
    }

    fun refresh() {
        scope.launch {
            val previousPoemId = poetryRepository.currentHome()?.poem?.id
            isRefreshing.value = true
            val result = poetryRepository.refresh(force = true)
            handleRefreshResult(result)
            if (result is RefreshResult.Success) {
                val currentPoemId = poetryRepository.currentHome()?.poem?.id
                if (currentPoemId != null && currentPoemId != previousPoemId) {
                    startRevealAnimation()
                } else {
                    stopRevealAnimation()
                }
            } else {
                stopRevealAnimation()
            }
            isRefreshing.value = false
        }
    }

    fun toggleExpand() {
        showFullText.value = !showFullText.value
    }

    fun openThemePicker() {
        isThemePickerVisible.value = true
    }

    fun closeThemePicker() {
        isThemePickerVisible.value = false
    }

    fun updateThemeSeed(hex: String) {
        scope.launch {
            settingsRepository.saveThemeSeed(hex)
            isThemePickerVisible.value = false
        }
    }

    fun dismissError() {
        errorMessage.value = null
    }

    private fun startRevealAnimation() {
        revealJob?.cancel()
        refreshAnimationKey.value += 1L
        isPoemRevealRunning.value = true
        revealJob = scope.launch {
            delay(500)
            isPoemRevealRunning.value = false
        }
    }

    private fun stopRevealAnimation() {
        revealJob?.cancel()
        isPoemRevealRunning.value = false
    }

    private fun handleRefreshResult(result: RefreshResult) {
        when (result) {
            RefreshResult.Success -> {
                isOfflineContent.value = false
                errorMessage.value = null
            }
            is RefreshResult.Offline -> {
                isOfflineContent.value = true
                errorMessage.value = result.message
            }
            is RefreshResult.Failure -> {
                isOfflineContent.value = false
                errorMessage.value = result.message
            }
        }
    }
}
