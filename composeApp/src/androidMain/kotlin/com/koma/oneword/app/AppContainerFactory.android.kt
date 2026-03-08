package com.koma.oneword.app

/**
 * Android-specific container factory that wires the SQLDelight driver with the application context.
 */

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.koma.oneword.database.OneWordDatabase

actual object AppContainerFactory {
    @Composable
    actual fun rememberAppContainer(): AppContainer {
        val context = LocalContext.current.applicationContext
        return remember(context) {
            AppContainer.create(
                driver = AndroidSqliteDriver(
                    schema = OneWordDatabase.Schema,
                    context = context,
                    name = "oneword.db",
                ),
            )
        }
    }
}
