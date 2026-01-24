package io.github.lycosmic.lithe.presentation.reader

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.lycosmic.lithe.R
import io.github.lycosmic.lithe.presentation.reader.components.BookReaderContent
import io.github.lycosmic.lithe.presentation.reader.components.ChapterListContent
import io.github.lycosmic.lithe.presentation.reader.components.ReaderBottomControls
import io.github.lycosmic.lithe.presentation.reader.components.ReaderTopBar
import io.github.lycosmic.lithe.ui.components.CircularWavyProgressIndicator
import io.github.lycosmic.lithe.util.toast
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce


@OptIn(FlowPreview::class)
@Composable
fun ReaderScreen(
    bookId: Long,
    navigateBack: () -> Unit,
    viewModel: ReaderViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 上下栏是否可见
    var isBarsVisible by remember { mutableStateOf(false) }

    // 书籍内容列表状态
    val contentListState = rememberLazyListState()

    // 章节列表状态
    val chapterListState = rememberLazyListState()

    val chapterName = remember(uiState.currentChapterIndex) {
        uiState.chapters.getOrNull(uiState.currentChapterIndex)?.title ?: "未知章节"
    }

    // 抽屉状态
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // 初始化
    LaunchedEffect(bookId) {
        viewModel.init(bookId)
    }

    LaunchedEffect(contentListState) {
        snapshotFlow { contentListState.firstVisibleItemIndex }
            .debounce(300)
            .collect { index ->
                // 屏幕最上方的 Item
                val currentItem = uiState.readerItems.getOrNull(index)
                if (currentItem != null) {
                    // 更新内存中的进度
                    viewModel.onEvent(ReaderEvent.OnScrollPositionChanged(currentItem))
                }
            }
    }

    // 用户突然退出或锁屏时强制保存
    DisposableEffect(Unit) {
        onDispose {
            val currentItem = uiState.readerItems.getOrNull(contentListState.firstVisibleItemIndex)
            // 强制立即保存内存中的进度
            viewModel.onEvent(ReaderEvent.OnStopOrDispose(currentItem))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                ReaderEffect.ShowOrHideTopBarAndBottomControl -> {
                    isBarsVisible = !isBarsVisible
                }

                ReaderEffect.NavigateBack -> {
                    navigateBack()
                }

                ReaderEffect.HideChapterListDrawer -> {
                    if (drawerState.isOpen) {
                        drawerState.close()
                    }
                }

                ReaderEffect.ShowChapterListDrawer -> {
                    if (drawerState.isClosed) {
                        drawerState.open()
                    }
                }

                is ReaderEffect.RestoreFocus -> TODO()
                is ReaderEffect.ScrollToItem -> {
                    val index = effect.index
                    contentListState.scrollToItem(index)
                }

                ReaderEffect.ShowFirstChapterToast -> {
                    R.string.first_chapter.toast()
                }

                ReaderEffect.ShowLastChapterToast -> {
                    R.string.last_chapter.toast()
                }

                ReaderEffect.ShowLoadChapterContentFailedToast -> {
                    R.string.load_chapter_content_failed.toast()
                }

                is ReaderEffect.ScrollToChapter -> {
                    chapterListState.scrollToItem(effect.index)
                }
            }
        }
    }

    BackHandler(enabled = drawerState.isOpen) {
        viewModel.onEvent(ReaderEvent.OnDrawerBackClick)
    }


    // 侧滑章节菜单
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .fillMaxSize(),
                drawerShape = RectangleShape,
                windowInsets = WindowInsets(0, 0, 0, 0)
            ) {
                ChapterListContent(
                    onChapterClick = { index ->
                        // 跳转章节
                        viewModel.onEvent(
                            ReaderEvent.OnChapterItemClick(
                                index
                            )
                        )
                    },
                    chapters = uiState.chapters,
                    currentChapterIndex = uiState.currentChapterIndex,
                    currentChapterProgressText = "0%",
                    state = chapterListState
                )
            }
        },
        scrimColor = DrawerDefaults.scrimColor, // 遮罩颜色
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen, // 只有当抽屉打开时，才允许使用手势返回
    ) {
        // 屏幕主内容
        DrawerContent(
            uiState = uiState,
            isBarsVisible = isBarsVisible,
            listState = contentListState,
            chapterName = chapterName,
            onBackClick = {
                viewModel.onEvent(ReaderEvent.OnBackClick)
            },
            onReadContentClick = {
                viewModel.onEvent(ReaderEvent.OnReadContentClick)
            },
            onChapterMenuClick = {
                viewModel.onEvent(ReaderEvent.OnChapterMenuClick)
            },
            onPrevClick = {
                viewModel.onEvent(ReaderEvent.OnPrevChapterClick)
            },
            onNextClick = {
                viewModel.onEvent(ReaderEvent.OnNextChapterClick)
            },
        )
    }

}

/**
 * 抽屉内容
 */
@Composable
fun DrawerContent(
    modifier: Modifier = Modifier,
    uiState: ReaderState,
    chapterName: String,
    isBarsVisible: Boolean,
    onBackClick: () -> Unit,
    onReadContentClick: () -> Unit,
    onChapterMenuClick: () -> Unit,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    listState: LazyListState
) {

    val isPrevVisible = remember(uiState.currentChapterIndex, uiState.chapters.size) {
        if (uiState.currentChapterIndex < 0 || uiState.currentChapterIndex >= uiState.chapters.size) {
            false
        } else {
            uiState.currentChapterIndex > 0
        }
    }
    val isNextVisible = remember(uiState.currentChapterIndex, uiState.chapters.size) {
        if (uiState.currentChapterIndex < 0 || uiState.currentChapterIndex >= uiState.chapters.size) {
            false
        } else {
            uiState.currentChapterIndex < uiState.chapters.size - 1
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface) // 模拟纸张颜色
    ) {
        // 阅读文本
        if (uiState.isInitialLoading) {
            CircularWavyProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            BookReaderContent(
                contents = uiState.readerItems,
                listState = listState,
                onContentClick = onReadContentClick
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
                bookName = uiState.book.title,
                chapterName = chapterName,
                chapterProgress = uiState.chapterProgressPercent,
                onBackClick = onBackClick,
                onChapterMenuClick = onChapterMenuClick,
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
                isPrevVisible = isPrevVisible,
                isNextVisible = isNextVisible,
                onPrevClick = onPrevClick,
                onNextClick = onNextClick,
            )
        }
    }
}

