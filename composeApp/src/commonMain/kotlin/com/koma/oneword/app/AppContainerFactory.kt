package com.koma.oneword.app

/**
 * Platform abstraction for building the shared dependency container from each target.
 */

import androidx.compose.runtime.Composable

expect object AppContainerFactory {
    @Composable
    fun rememberAppContainer(): AppContainer
}
