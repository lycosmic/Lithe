package io.github.lycosmic.lithe.presentation.reader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.lycosmic.lithe.presentation.reader.components.BookReaderContent
import io.github.lycosmic.lithe.presentation.reader.components.ReaderEffect
import io.github.lycosmic.lithe.presentation.reader.components.ReaderEvent
import io.github.lycosmic.lithe.ui.components.CircularWavyProgressIndicator


@Composable
fun ReaderScreen(
    bookId: Long,
    navigateBack: () -> Unit,
    viewModel: ReaderViewModel = hiltViewModel(),
) {
    // 初始加载书籍
    LaunchedEffect(bookId) {
        viewModel.loadBook(bookId)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 上下栏是否可见
    var isBarsVisible by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()


    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                ReaderEffect.ShowOrHideTopBarAndBottomControl -> {
                    isBarsVisible = !isBarsVisible
                }

                else -> {
                    navigateBack()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface) // 模拟纸张颜色
    ) {
        // 阅读文本
        if (uiState.isLoading) {
            CircularWavyProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            BookReaderContent(
                contents = uiState.currentContent,
                listState = listState,
                onContentClick = {
                    viewModel.onEvent(ReaderEvent.OnContentClick)
                }
            )
        }

        // 顶部工具栏
        AnimatedVisibility(
            isBarsVisible,
            enter = fadeIn() + slideInVertically { fullHeight ->
                -fullHeight
            },
            exit = fadeOut() + slideOutVertically { fullHeight ->
                // 向上滑出隐藏
                -fullHeight
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .wrapContentHeight(), // 高度仅随内容变化
        ) {
            ReaderTopBar(
                bookName = "",
                chapterName = "",
                chapterProgress = 0f,
                onBackClick = {
                    viewModel.onEvent(ReaderEvent.OnBackClick)
                },
                onChapterMenuClick = {

                },
                onReaderSettingsClick = {

                }
            )
        }

        // 底部控制层
        AnimatedVisibility(
            isBarsVisible,
            enter = fadeIn() + slideInVertically { fullHeight ->
                fullHeight
            },
            exit = fadeOut() + slideOutVertically { fullHeight ->
                fullHeight
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .wrapContentHeight(), // 高度仅随内容变化
        ) {
            ReaderBottomControls(
                bookProgress = 0f,
                chapterProgress = 0f,
                onProgressChange = {

                },
                currentChapterIndex = 0,
                isPrevVisible = false,
                isNextVisible = false,
                onPrevClick = {

                },
                onNextClick = {

                },
            )
        }
    }
}


@Composable
fun ReaderTopBar(
    bookName: String, // 书籍名称
    chapterName: String, // 章节名称
    chapterProgress: Float, // 章节进度
    onBackClick: () -> Unit,
    onChapterMenuClick: () -> Unit, // 章节菜单
    onReaderSettingsClick: () -> Unit, // 阅读设置
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f), // 半透明背景
    ) {
        Row(
            modifier = Modifier
                .statusBarsPadding() // 避开系统状态栏
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
            }

            Spacer(Modifier.width(16.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = bookName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = chapterName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            IconButton(onClick = onChapterMenuClick) {
                Icon(Icons.Default.Menu, contentDescription = null)
            }
            Spacer(Modifier.width(16.dp))
            IconButton(onClick = onReaderSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = null)
            }
        }
    }
}

@Composable
fun ReaderBottomControls(
    bookProgress: Float,
    chapterProgress: Float,
    onProgressChange: (Float) -> Unit,
    currentChapterIndex: Int, // 当前第几章
    isPrevVisible: Boolean,
    isNextVisible: Boolean,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val text = remember(bookProgress, chapterProgress) {
        "${bookProgress * 100}% (${chapterProgress * 100}%)"
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding() // 避开底部导航栏
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 进度百分比文字
            Text(text, style = MaterialTheme.typography.bodyLarge)

            Spacer(Modifier.height(8.dp))

            // 进度条区域
            Row(verticalAlignment = Alignment.CenterVertically) {
                AnimatedVisibility(isPrevVisible) {
                    IconButton(onClick = onPrevClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
                Slider(
                    value = bookProgress,
                    onValueChange = onProgressChange,
                    modifier = Modifier.weight(1f)
                )
                AnimatedVisibility(isNextVisible) {
                    IconButton(onClick = onNextClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, null)
                    }
                }
            }
        }
    }


}