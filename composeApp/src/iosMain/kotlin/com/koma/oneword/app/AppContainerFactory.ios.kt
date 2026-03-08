package com.koma.oneword.app

/**
 * iOS-specific container factory that maps SQLDelight storage into Application Support.
 */

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.koma.oneword.database.OneWordDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import co.touchlab.sqliter.DatabaseConfiguration
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual object AppContainerFactory {
    @OptIn(ExperimentalForeignApi::class)
    @Composable
    actual fun rememberAppContainer(): AppContainer {
        return remember { createIosAppContainer() }
    }
}

@OptIn(ExperimentalForeignApi::class)
fun createIosAppContainer(): AppContainer {
    val fileManager = NSFileManager.defaultManager
    val appSupportUrl = fileManager.URLForDirectory(
        directory = NSApplicationSupportDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = true,
        error = null,
    ) ?: error("Failed to resolve Application Support directory")
    val databaseUrl: NSURL = appSupportUrl.URLByAppendingPathComponent("oneword.db")!!
    // SQLiter expects the file name and base directory separately on Apple targets.
    val basePath = databaseUrl.path?.substringBeforeLast("/", "") ?: error("Failed to resolve database base path")
    val driver = NativeSqliteDriver(
        schema = OneWordDatabase.Schema,
        name = "oneword.db",
        onConfiguration = { configuration ->
            configuration.copy(
                extendedConfig = configuration.extendedConfig.copy(
                    basePath = basePath,
                ),
            )
        },
    )
    return AppContainer.create(driver)
}
