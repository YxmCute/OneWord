package com.koma.oneword.ui.settings

/**
 * Settings screen for theme mode, seed color access, and app metadata.
 */

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.koma.oneword.model.AppThemeSettings
import com.koma.oneword.model.ThemeMode
import com.koma.oneword.theme.LocalOneWordTheme
import com.koma.oneword.theme.toColorOrDefault
import com.koma.oneword.ui.AppInsetMode
import com.koma.oneword.ui.contentInsets
import com.koma.oneword.ui.headerInsets
import com.koma.oneword.ui.components.PosterBackground

@Composable
fun SettingsScreen(
    settings: AppThemeSettings,
    insetMode: AppInsetMode,
    onBack: () -> Unit,
    onUpdateMode: (ThemeMode) -> Unit,
    onOpenThemePicker: () -> Unit,
) {
    val scheme = LocalOneWordTheme.current.scheme

    PosterBackground(scheme = scheme) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(insetMode.contentInsets())
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 620.dp),
                shape = RoundedCornerShape(34.dp),
                color = scheme.paperCard,
                shadowElevation = 28.dp,
                tonalElevation = 0.dp,
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .windowInsetsPadding(insetMode.headerInsets()),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "设置",
                            style = MaterialTheme.typography.headlineMedium,
                            color = scheme.primaryText,
                        )
                        OutlinedButton(onClick = onBack) { Text("返回") }
                    }

                    Text(
                        text = "主题模式",
                        style = MaterialTheme.typography.titleMedium,
                        color = scheme.primaryText,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ThemeMode.entries.forEach { mode ->
                            val selected = settings.mode == mode
                            if (selected) {
                                Button(onClick = { onUpdateMode(mode) }) { Text(mode.name) }
                            } else {
                                OutlinedButton(onClick = { onUpdateMode(mode) }) { Text(mode.name) }
                            }
                        }
                    }

                    Text(
                        text = "主色",
                        style = MaterialTheme.typography.titleMedium,
                        color = scheme.primaryText,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            androidx.compose.foundation.layout.Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(settings.seedHex.toColorOrDefault()),
                            )
                            Text(settings.seedHex, color = scheme.secondaryText)
                        }
                        Button(onClick = onOpenThemePicker) { Text("打开取色板") }
                    }

                    Text(
                        text = "数据来源",
                        style = MaterialTheme.typography.titleMedium,
                        color = scheme.primaryText,
                    )
                    Text(
                        text = "今日诗词接口：先获取 token，再带 X-User-Token 请求 sentence。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = scheme.secondaryText,
                    )

                    Text(
                        text = "版本 1.0.0",
                        style = MaterialTheme.typography.bodyMedium,
                        color = scheme.secondaryText,
                    )
                }
            }
        }
    }
}
