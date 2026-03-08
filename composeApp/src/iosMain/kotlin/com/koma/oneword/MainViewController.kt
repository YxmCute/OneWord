package com.koma.oneword

/**
 * iOS entry point that hosts the shared Compose UI inside a UIViewController.
 */

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController(
    configure = {
        enforceStrictPlistSanityCheck = false
    }
) {
    OneWordApp()
}
