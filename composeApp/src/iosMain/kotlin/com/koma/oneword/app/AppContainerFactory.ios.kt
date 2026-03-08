package com.koma.oneword.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.koma.oneword.database.OneWordDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual object AppContainerFactory {
    @OptIn(ExperimentalForeignApi::class)
    @Composable
    actual fun rememberAppContainer(): AppContainer {
        return remember {
            println("iOS AppContainerFactory: create start")
            val fileManager = NSFileManager.defaultManager
            val appSupportUrl = fileManager.URLForDirectory(
                directory = NSApplicationSupportDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = true,
                error = null,
            ) ?: error("Failed to resolve Application Support directory")
            val databaseUrl: NSURL = appSupportUrl.URLByAppendingPathComponent("oneword.db")!!
            println("iOS AppContainerFactory: database path = ${databaseUrl.path}")
            val driver = NativeSqliteDriver(
                schema = OneWordDatabase.Schema,
                name = databaseUrl.path ?: "oneword.db",
            )
            AppContainer.create(driver).also {
                println("iOS AppContainerFactory: create success")
            }
        }
    }
}
