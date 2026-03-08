package com.koma.oneword.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.koma.oneword.presentation.HomeUiState
import com.koma.oneword.theme.LocalOneWordTheme
import com.koma.oneword.ui.components.PosterBackground

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onRefresh: () -> Unit,
    onToggleExpand: () -> Unit,
    onOpenThemePicker: () -> Unit,
    onOpenSettings: () -> Unit,
    onDismissError: () -> Unit,
) {
    val scheme = LocalOneWordTheme.current.scheme

    PosterBackground(scheme = scheme) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "ONEWORD",
                    color = scheme.secondaryText,
                    style = MaterialTheme.typography.labelLarge,
                    letterSpacing = 5.sp,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedButton(onClick = onOpenThemePicker) { Text("取色") }
                    OutlinedButton(onClick = onOpenSettings) { Text("设置") }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 760.dp),
                    shape = RoundedCornerShape(34.dp),
                    shadowElevation = 30.dp,
                    color = scheme.paperCard,
                    tonalElevation = 0.dp,
                ) {
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 28.dp, vertical = 30.dp),
                    ) {
                        val heroFont = when {
                            maxWidth < 420.dp -> 38.sp
                            maxWidth < 640.dp -> 52.sp
                            else -> 68.sp
                        }
                        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                            when {
                                uiState.poem != null -> {
                                    val poem = uiState.poem
                                    Text(
                                        text = poem.content,
                                        color = scheme.primaryText,
                                        fontSize = heroFont,
                                        lineHeight = heroFont * 1.06f,
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                    )
                                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                        Text(
                                            text = poem.author,
                                            color = scheme.secondaryText,
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.Light,
                                        )
                                        Text(
                                            text = "《${poem.title}》 · ${poem.dynasty}",
                                            color = scheme.secondaryText,
                                            style = MaterialTheme.typography.titleMedium,
                                        )
                                    }

                                    if (uiState.error != null) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(scheme.accentMuted, RoundedCornerShape(18.dp))
                                                .padding(horizontal = 14.dp, vertical = 10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Text(
                                                text = uiState.error,
                                                color = scheme.primaryText,
                                                modifier = Modifier.weight(1f),
                                            )
                                            OutlinedButton(onClick = onDismissError) { Text("知道了") }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                    OutlinedButton(onClick = onToggleExpand) {
                                        Text(if (uiState.showFullText) "收起全文" else "展开全文")
                                    }

                                    AnimatedVisibility(uiState.showFullText) {
                                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                            Text(
                                                text = poem.fullText.joinToString("\n"),
                                                color = scheme.primaryText,
                                                style = MaterialTheme.typography.bodyLarge,
                                                lineHeight = 30.sp,
                                            )
                                            if (poem.translation.isNotEmpty()) {
                                                Text(
                                                    text = "译文",
                                                    color = scheme.primaryText,
                                                    style = MaterialTheme.typography.titleSmall,
                                                )
                                                Text(
                                                    text = poem.translation.joinToString("\n\n"),
                                                    color = scheme.secondaryText,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    lineHeight = 26.sp,
                                                )
                                            }
                                        }
                                    }
                                }
                                uiState.isLoading -> {
                                    Text(
                                        text = "正在取一首诗。",
                                        color = scheme.primaryText,
                                        style = MaterialTheme.typography.displaySmall,
                                        fontFamily = FontFamily.Serif,
                                    )
                                    Text(
                                        text = "首次启动会先建立 token，然后拉取今日诗句。",
                                        color = scheme.secondaryText,
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                }
                                uiState.poem == null && uiState.error != null -> {
                                    Text(
                                        text = "内容暂时没有加载出来。",
                                        color = scheme.primaryText,
                                        style = MaterialTheme.typography.displaySmall,
                                        fontFamily = FontFamily.Serif,
                                    )
                                    Text(
                                        text = uiState.error,
                                        color = scheme.secondaryText,
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                    Button(onClick = onRefresh) { Text("重试") }
                                }
                                else -> {
                                    Text(
                                        text = "正在整理诗句。",
                                        color = scheme.primaryText,
                                        style = MaterialTheme.typography.displaySmall,
                                        fontFamily = FontFamily.Serif,
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "OneWord.",
                        color = scheme.primaryText,
                        style = MaterialTheme.typography.displaySmall,
                        fontFamily = FontFamily.Serif,
                    )
                    Button(onClick = onRefresh, enabled = !uiState.isRefreshing) {
                        Text(if (uiState.isRefreshing) "刷新中" else "refresh")
                    }
                }
                Text(
                    text = buildString {
                        if (uiState.isOfflineContent) append("离线内容")
                        uiState.lastUpdatedText?.let {
                            if (isNotEmpty()) append("  ·  ")
                            append("更新于 $it")
                        }
                    }.ifBlank { "等待加载" },
                    color = scheme.secondaryText,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Start,
                )
            }
        }
    }
}
