package com.koma.oneword.app

/**
 * Desktop-specific container factory that stores the SQLDelight database in Application Support.
 */

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.koma.oneword.database.OneWordDatabase
import java.io.File

actual object AppContainerFactory {
    @Composable
    actual fun rememberAppContainer(): AppContainer {
        return remember {
            val appDir = File(System.getProperty("user.home"), "Library/Application Support/OneWord")
            appDir.mkdirs()
            val databaseFile = File(appDir, "oneword.db")
            val firstLaunch = !databaseFile.exists()
            val driver = JdbcSqliteDriver("jdbc:sqlite:${databaseFile.absolutePath}")
            if (firstLaunch) {
                OneWordDatabase.Schema.create(driver)
            }
            AppContainer.create(driver)
        }
    }
}
