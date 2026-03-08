package com.koma.oneword

/**
 * Android launcher activity that enables edge-to-edge and mounts the shared app UI.
 */

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.koma.oneword.ui.AppInsetMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Let the poster background extend through the system bars on Android.
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false
            isAppearanceLightNavigationBars = false
        }
        setContent {
            OneWordApp(insetMode = AppInsetMode.IMMERSIVE_TOP)
        }
    }
}
