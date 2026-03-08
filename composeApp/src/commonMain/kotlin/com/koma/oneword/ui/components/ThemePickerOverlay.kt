package com.koma.oneword.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.koma.oneword.theme.LocalOneWordTheme
import com.koma.oneword.theme.ThemeDefaults
import com.koma.oneword.theme.colorFromHsv
import com.koma.oneword.theme.toColorOrDefault
import com.koma.oneword.theme.toHex
import com.koma.oneword.theme.toHsv

@Composable
fun ThemePickerOverlay(
    initialSeedHex: String,
    onPreview: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    val scheme = LocalOneWordTheme.current.scheme
    val initialColor = remember(initialSeedHex) { initialSeedHex.toColorOrDefault() }
    val initialHsv = remember(initialColor) { initialColor.toHsv() }
    var hue by remember(initialSeedHex) { mutableFloatStateOf(initialHsv.first) }
    var saturation by remember(initialSeedHex) { mutableFloatStateOf(initialHsv.second) }
    var brightness by remember(initialSeedHex) { mutableFloatStateOf(initialHsv.third) }

    val previewColor = remember(hue, saturation, brightness) {
        colorFromHsv(hue = hue, saturation = saturation, value = brightness)
    }
    val previewHex = remember(previewColor) { previewColor.toHex() }

    LaunchedEffect(previewHex) {
        onPreview(previewHex)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.38f))
            .clickable(onClick = onDismiss, indication = null, interactionSource = remember { MutableInteractionSource() }),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(28.dp))
                .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { },
            color = scheme.paperCard,
            tonalElevation = 0.dp,
            shadowElevation = 16.dp,
            shape = RoundedCornerShape(28.dp),
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Text(
                    text = "主题取色",
                    style = MaterialTheme.typography.headlineSmall,
                    color = scheme.primaryText,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = "选择你的首页主色，背景、按钮和边框会实时联动。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.secondaryText,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(previewColor),
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = previewHex, color = scheme.primaryText, style = MaterialTheme.typography.titleMedium)
                        Text(text = "实时预览已开启", color = scheme.secondaryText, style = MaterialTheme.typography.bodySmall)
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(text = "预设色板", color = scheme.primaryText, style = MaterialTheme.typography.titleSmall)
                    ThemeDefaults.Presets.chunked(4).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            row.forEach { preset ->
                                val color = preset.toColorOrDefault()
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .clickable {
                                            val hsv = color.toHsv()
                                            hue = hsv.first
                                            saturation = hsv.second
                                            brightness = hsv.third
                                        },
                                )
                            }
                        }
                    }
                }

                PickerSlider(
                    label = "Hue",
                    value = hue,
                    valueRange = 0f..360f,
                    onValueChange = { hue = it },
                    scheme = scheme,
                )
                PickerSlider(
                    label = "Saturation",
                    value = saturation,
                    valueRange = 0f..1f,
                    onValueChange = { saturation = it },
                    scheme = scheme,
                )
                PickerSlider(
                    label = "Brightness",
                    value = brightness,
                    valueRange = 0f..1f,
                    onValueChange = { brightness = it },
                    scheme = scheme,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            val fallback = ThemeDefaults.DefaultSeedHex.toColorOrDefault().toHsv()
                            hue = fallback.first
                            saturation = fallback.second
                            brightness = fallback.third
                        },
                    ) {
                        Text("恢复默认")
                    }
                    OutlinedButton(
                        modifier = Modifier.weight(1f),
                        onClick = onDismiss,
                    ) {
                        Text("取消")
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { onConfirm(previewHex) },
                    ) {
                        Text("保存")
                    }
                }
            }
        }
    }
}

@Composable
private fun PickerSlider(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit,
    scheme: com.koma.oneword.theme.AppColorScheme,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "$label  ${displayValue(value)}",
            color = scheme.primaryText,
            style = MaterialTheme.typography.titleSmall,
        )
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
        )
    }
}

private fun displayValue(value: Float): String {
    val rounded = (value * 100).toInt() / 100.0
    return rounded.toString()
}
