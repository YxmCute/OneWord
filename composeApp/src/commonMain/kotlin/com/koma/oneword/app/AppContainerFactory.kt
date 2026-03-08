package com.koma.oneword.app

import androidx.compose.runtime.Composable

expect object AppContainerFactory {
    @Composable
    fun rememberAppContainer(): AppContainer
}
