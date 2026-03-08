package com.koma.oneword.ui.home

/**
 * Poster-style home screen that renders the poem, metadata, expansion panel, and refresh animation.
 */

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.koma.oneword.presentation.HomeUiState
import com.koma.oneword.theme.LocalOneWordTheme
import com.koma.oneword.ui.AppInsetMode
import com.koma.oneword.ui.contentInsets
import com.koma.oneword.ui.headerInsets
import com.koma.oneword.ui.components.PosterBackground
import kotlin.math.roundToInt

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    insetMode: AppInsetMode,
    onRefresh: () -> Unit,
    onToggleExpand: () -> Unit,
    onOpenThemePicker: () -> Unit,
    onOpenSettings: () -> Unit,
    onDismissError: () -> Unit,
) {
    val scheme = LocalOneWordTheme.current.scheme
    // Metadata and the full-text region stay hidden while the main sentence reveal is running.
    val showMetadata = !uiState.isPoemRevealRunning

    PosterBackground(scheme = scheme) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(insetMode.contentInsets())
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(insetMode.headerInsets()),
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
                    .weight(1f)
                    .padding(top = 20.dp),
                // Keep the poster card top-anchored so expanding/collapsing the full text does
                // not re-center the whole card and look like a dropped frame.
                contentAlignment = Alignment.TopCenter,
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
                            .animateContentSize(tween(durationMillis = 100))
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
                                    AnimatedPoemText(
                                        text = poem.content,
                                        fontSize = heroFont,
                                        lineHeight = heroFont * 1.06f,
                                        color = scheme.primaryText,
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        animationKey = uiState.refreshAnimationKey,
                                        shouldAnimate = uiState.isPoemRevealRunning,
                                    )

                                    if (showMetadata) {
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

                                        AnimatedVisibility(
                                            visible = uiState.showFullText,
                                            // Collapse from the top so the last line disappears
                                            // smoothly instead of snapping the card height.
                                            enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(tween(200)),
                                            exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut(tween(200)),
                                        ) {
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
                                uiState.error != null -> {
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

@Composable
private fun AnimatedPoemText(
    text: String,
    animationKey: Long,
    shouldAnimate: Boolean,
    fontSize: androidx.compose.ui.unit.TextUnit,
    lineHeight: androidx.compose.ui.unit.TextUnit,
    color: androidx.compose.ui.graphics.Color,
    fontFamily: FontFamily,
    fontWeight: FontWeight,
) {
    val progress = remember(text, animationKey) {
        Animatable(if (shouldAnimate) 0f else 1f)
    }

    LaunchedEffect(text, animationKey, shouldAnimate) {
        if (shouldAnimate) {
            progress.snapTo(0f)
            progress.animateTo(
                targetValue = 1f,
                // Fixed-duration typing so refresh feedback feels deliberate rather than random.
                animationSpec = tween(durationMillis = 500, easing = LinearEasing),
            )
        } else {
            progress.snapTo(1f)
        }
    }

    val visibleChars = if (shouldAnimate) {
        (text.length * progress.value).roundToInt().coerceIn(0, text.length)
    } else {
        text.length
    }
    val renderedText = text.take(visibleChars.coerceAtLeast(if (progress.value > 0f) 1 else 0))

    Text(
        text = renderedText,
        color = color,
        fontSize = fontSize,
        lineHeight = lineHeight,
        fontFamily = fontFamily,
        fontWeight = fontWeight,
        modifier = Modifier.alpha(if (shouldAnimate) progress.value else 1f),
    )
}
