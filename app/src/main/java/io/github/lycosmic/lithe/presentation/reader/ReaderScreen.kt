package io.github.lycosmic.lithe.presentation.reader

import android.app.Activity
import android.view.WindowManager
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.lycosmic.data.settings.AppChapterTitleAlign
import io.github.lycosmic.data.settings.AppImageAlign
import io.github.lycosmic.data.settings.AppTextAlign
import io.github.lycosmic.data.settings.ImageColorEffect
import io.github.lycosmic.data.settings.ProgressTextAlign
import io.github.lycosmic.data.settings.ReadingMode
import io.github.lycosmic.domain.model.AppFontWeight
import io.github.lycosmic.domain.model.ColorPreset
import io.github.lycosmic.lithe.R
import io.github.lycosmic.lithe.presentation.reader.components.BookReaderContent
import io.github.lycosmic.lithe.presentation.reader.components.ChapterListContent
import io.github.lycosmic.lithe.presentation.reader.components.ReaderBottomControls
import io.github.lycosmic.lithe.presentation.reader.components.ReaderSettingsBottomSheet
import io.github.lycosmic.lithe.presentation.reader.components.ReaderSettingsTabType
import io.github.lycosmic.lithe.presentation.reader.components.ReaderTopBar
import io.github.lycosmic.lithe.ui.components.CircularWavyProgressIndicator
import io.github.lycosmic.lithe.util.FormatUtils.formatProgress
import io.github.lycosmic.lithe.util.toast
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlin.math.abs


@OptIn(FlowPreview::class)
@Composable
fun ReaderScreen(
    bookId: Long,
    navigateBack: () -> Unit,
    viewModel: ReaderViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val window = (context as? Activity)?.window

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // 字体设置
    val fonts by viewModel.availableFonts.collectAsStateWithLifecycle()
    val fontId by viewModel.fontId.collectAsStateWithLifecycle()
    val fontSize by viewModel.fontSize.collectAsStateWithLifecycle()
    val fontWeight by viewModel.fontWeight.collectAsStateWithLifecycle()
    val isItalic by viewModel.isItalic.collectAsStateWithLifecycle()
    val letterSpacing by viewModel.letterSpacing.collectAsStateWithLifecycle()
    val fontFamily by viewModel.fontFamily.collectAsStateWithLifecycle()

    // 文本设置
    val appTextAlign by viewModel.appTextAlign.collectAsStateWithLifecycle()
    val lineHeight by viewModel.lineHeight.collectAsStateWithLifecycle()
    val paragraphSpacing by viewModel.paragraphSpacing.collectAsStateWithLifecycle()
    val paragraphIndent by viewModel.paragraphIndent.collectAsStateWithLifecycle()

    // 图片设置
    val imageVisible by viewModel.imageVisible.collectAsStateWithLifecycle()
    val imageCaptionVisible by viewModel.imageCaptionVisible.collectAsStateWithLifecycle()
    val imageColorEffect by viewModel.imageColorEffect.collectAsStateWithLifecycle()
    val imageCornerRadius by viewModel.imageCornerRadius.collectAsStateWithLifecycle()
    val imageAlign by viewModel.imageAlign.collectAsStateWithLifecycle()
    val imageSizePercent by viewModel.imageSizePercent.collectAsStateWithLifecycle()

    // 章节设置
    val chapterTitleAlign by viewModel.chapterTitleAlign.collectAsStateWithLifecycle()

    // 边距设置
    val sidePadding by viewModel.sidePadding.collectAsStateWithLifecycle()
    val verticalPadding by viewModel.verticalPadding.collectAsStateWithLifecycle()
    val cutoutPaddingApply by viewModel.cutoutPaddingApply.collectAsStateWithLifecycle()
    val bottomBarPadding by viewModel.bottomBarPadding.collectAsStateWithLifecycle()

    // 杂项设置
    val isFullScreen by viewModel.isFullScreen.collectAsStateWithLifecycle()
    val screenAlive by viewModel.screenAlive.collectAsStateWithLifecycle()
    val isHideBarOnQuickScroll by viewModel.isHideBarOnQuickScroll.collectAsStateWithLifecycle()

    // 系统设置
    val isCustomBrightness by viewModel.isCustomBrightness.collectAsStateWithLifecycle()
    val customBrightnessValue by viewModel.customBrightnessValue.collectAsStateWithLifecycle()
    val screenOrientation by viewModel.screenOrientation.collectAsStateWithLifecycle()

    // 进度设置
    val isBottomProgressTextVisible by viewModel.isBottomProgressTextVisible.collectAsStateWithLifecycle()
    val progressTextSize by viewModel.progressTextSize.collectAsStateWithLifecycle()
    val bottomProgressPadding by viewModel.bottomProgressPadding.collectAsStateWithLifecycle()
    val bottomProgressTextAlign by viewModel.bottomProgressTextAlign.collectAsStateWithLifecycle()

    // 颜色预设设置
    val colorPresets by viewModel.colorPresets.collectAsStateWithLifecycle()
    val currentColorPreset by viewModel.currentColorPreset.collectAsStateWithLifecycle()
    val isQuickColorPresetChangeEnabled by viewModel.isQuickColorPresetChangeEnabled.collectAsStateWithLifecycle()

    // 阅读模式
    val readingMode by viewModel.readingMode.collectAsStateWithLifecycle()

    // 上下栏是否可见
    var isBarsVisible by remember { mutableStateOf(false) }

    // 书籍内容列表状态
    val contentListState = rememberLazyListState()
    // 列表状态变化时更新阅读进度
    LaunchedEffect(contentListState) {
        snapshotFlow {
            contentListState.firstVisibleItemIndex
        }.debounce(300).collect { _ ->
            viewModel.onEvent(ReaderEvent.OnContentListStateChange(contentListState))
        }
    }


    // 章节列表状态
    val chapterListState = rememberLazyListState()

    val chapterName = remember(uiState.currentChapterIndex) {
        uiState.chapters.getOrNull(uiState.currentChapterIndex)?.title ?: "未知章节"
    }

    // 抽屉状态
    val chapterDrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    // 阅读设置底部抽屉是否可见
    var readerSettingsSheetVisible by remember {
        mutableStateOf(false)
    }

    // 阅读设置当前标签页
    var currentSettingsTab by remember {
        mutableStateOf(ReaderSettingsTabType.GENERAL)
    }

    // 初始化
    LaunchedEffect(bookId) {
        viewModel.init(bookId)
    }

    DisposableEffect(isFullScreen) {
        window?.let {
            val insetsController = WindowCompat.getInsetsController(it, it.decorView)
            insetsController.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

            if (!isFullScreen) {
                insetsController.show(WindowInsetsCompat.Type.systemBars())
            } else {
                // 全屏隐藏状态栏
                insetsController.hide(WindowInsetsCompat.Type.systemBars())
            }
        }


        onDispose {
            window?.let {
                val insetsController = WindowCompat.getInsetsController(it, it.decorView)
                insetsController.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    // 定义一个速度阈值 (像素/秒)，防止轻微的滑动也触发
    val velocityThreshold = 70f
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                consumed.y.let { velocity ->
                    // 如果当前上下栏隐藏，无需处理
                    if (!isBarsVisible) {
                        return@let
                    }

                    // 如果未启用快速滚动时隐藏菜单，则返回
                    if (!isHideBarOnQuickScroll) {
                        return@let
                    }

                    // 速度不够
                    if (abs(velocity) < velocityThreshold) {
                        return@let
                    }

                    // 快速滑动隐藏菜单
                    viewModel.onEvent(ReaderEvent.OnBarsVisibleChange(false))
                }

                return super.onPostScroll(consumed, available, source)
            }
        }
    }

    // 自定义亮度
    LaunchedEffect(isCustomBrightness, customBrightnessValue) {
        window?.let {
            if (isCustomBrightness) {
                // 设置自定义亮度
                val layoutParams = it.attributes
                layoutParams.screenBrightness = (customBrightnessValue / 100f).coerceIn(0f, 1.0f)
                it.attributes = layoutParams
            } else {
                // 恢复系统亮度 -1.0f
                val layoutParams = it.attributes
                layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                it.attributes = layoutParams
            }
        }
    }

    // 组件销毁时以恢复系统亮度
    DisposableEffect(Unit) {
        onDispose {
            window?.let {
                val layoutParams = it.attributes
                layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                it.attributes = layoutParams
            }
        }
    }

    // 保持屏幕常亮
    DisposableEffect(screenAlive) {
        window?.run {
            if (screenAlive) {
                // 添加常亮标志
                addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                // 清除常亮标志
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }

        onDispose {
            // 退出阅读界面时自动关闭常亮，节省电量
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    // 用户突然退出或锁屏时强制保存
    DisposableEffect(Unit) {
        onDispose {
            // 强制立即保存内存中的进度
            viewModel.onEvent(ReaderEvent.OnStopOrDispose)
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
                    if (chapterDrawerState.isOpen) {
                        chapterDrawerState.close()
                    }
                }

                ReaderEffect.ShowChapterListDrawer -> {
                    if (chapterDrawerState.isClosed) {
                        chapterDrawerState.open()
                    }
                }

                is ReaderEffect.RestoreFocus -> {

                }

                is ReaderEffect.ScrollToItem -> {
                    // 确保数据不为空再滚动
                    if (contentListState.layoutInfo.totalItemsCount > 0) {
                        contentListState.scrollToItem(effect.index, effect.offset)
                    }
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

                is ReaderEffect.ColorPresetChanged -> {
                    val preset = effect.colorPreset
                    val presetName = preset.name.ifBlank {
                        "预设 ${preset.id}"
                    }
                    R.string.color_preset_changed.toast(presetName)
                }

                ReaderEffect.HideTopBarAndBottomControl -> {
                    isBarsVisible = false
                }
            }
        }
    }

    // 进度文本
    val drawerChapterProgressText = remember(uiState.currentChapterIndex, uiState.chapterProgress) {
        val progressText =
            formatProgress(progress = uiState.chapterProgress, digits = 0, withPercent = true)
        progressText
    }

    // 全书进度文本
    val bookProgressText = remember(uiState.currentChapterIndex, uiState.progress.progressPercent) {
        "(${
            formatProgress(
                progress = uiState.progress.progressPercent,
                digits = 2,
                withPercent = false
            )
        }%)"
    }

    // 章节进度文本
    val chapterProgressText = remember(uiState.currentChapterIndex, uiState.chapterProgress) {
        "(${
            formatProgress(
                progress = uiState.chapterProgress,
                digits = 2,
                withPercent = false
            )
        }%)"
    }


    val progressText = remember(chapterProgressText, bookProgressText) {
        "$bookProgressText $chapterProgressText"
    }

    BackHandler(enabled = chapterDrawerState.isOpen) {
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
                    currentChapterProgressText = drawerChapterProgressText,
                    state = chapterListState
                )
            }
        },
        scrimColor = DrawerDefaults.scrimColor, // 遮罩颜色
        drawerState = chapterDrawerState,
        gesturesEnabled = chapterDrawerState.isOpen, // 只有当抽屉打开时，才允许使用手势返回
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
            onReaderSettingsClick = {
                readerSettingsSheetVisible = true
            },
            onPrevClick = {
                viewModel.onEvent(ReaderEvent.OnPrevChapterClick)
            },
            onNextClick = {
                viewModel.onEvent(ReaderEvent.OnNextChapterClick)
            },
            // 字体设置
            fontFamily = fontFamily,
            fontSize = fontSize,
            fontWeight = fontWeight,
            isItalic = isItalic,
            letterSpacing = letterSpacing,
            // 文本设置
            appTextAlign = appTextAlign,
            lineHeight = lineHeight,
            paragraphSpacing = paragraphSpacing,
            paragraphIndent = paragraphIndent,
            // 图片设置
            imageVisible = imageVisible,
            imageCaptionVisible = imageCaptionVisible,
            imageColorEffect = imageColorEffect,
            imageCornerRadius = imageCornerRadius,
            imageAlign = imageAlign,
            imageSizePercent = imageSizePercent,
            // 章节设置
            chapterTitleAlign = chapterTitleAlign,
            // 边距设置
            sidePadding = sidePadding,
            verticalPadding = verticalPadding,
            cutoutPaddingApply = cutoutPaddingApply,
            bottomBarPadding = bottomBarPadding,
            colorPreset = currentColorPreset,
            presetChangeEnabled = isQuickColorPresetChangeEnabled,
            swipeLastColorPreset = {
                viewModel.onEvent(ReaderEvent.OnSwipeLastColorPreset)
            },
            swipeNextColorPreset = {
                viewModel.onEvent(ReaderEvent.OnSwipeNextColorPreset)
            },
            fullScreen = isFullScreen,
            progressText = progressText,
            progressTextVisible = isBottomProgressTextVisible,
            progressTextFontSize = progressTextSize,
            progressTextPadding = bottomProgressPadding,
            progressTextAlign = bottomProgressTextAlign,
            nestedScrollConnection = nestedScrollConnection,
            onProgressChange = {
                viewModel.onEvent(ReaderEvent.OnBookProgressChange(it))
            },
            readingMode = readingMode
        )


        // 阅读设置底部抽屉
        ReaderSettingsBottomSheet(
            visible = readerSettingsSheetVisible,
            onDismissRequest = {
                // 实现关闭逻辑
                readerSettingsSheetVisible = false
            },
            currentTab = currentSettingsTab, // 添加状态管理
            onTabChange = { newTab ->
                // 实现标签切换逻辑
                currentSettingsTab = newTab
            },
            currentColorPreset = currentColorPreset,
            onColorPresetChange = { preset ->
                // 实现颜色预设切换
                viewModel.onEvent(ReaderEvent.OnColorPresetClick(preset))
            },

            // GeneralContent 参数
            pageTurnMode = readingMode,
            onPageTurnModeChange = { mode ->
                // 实现翻页模式切换
                viewModel.onEvent(ReaderEvent.OnReadingModeChange(mode))
            },
            sidePadding = sidePadding,
            onSidePaddingChange = { padding ->
                // 实现侧边填充变更
                viewModel.onEvent(ReaderEvent.OnSidePaddingChange(padding))
            },
            verticalPadding = verticalPadding,
            onVerticalPaddingChange = { padding ->
                // 实现垂直填充变更
                viewModel.onEvent(ReaderEvent.OnVerticalPaddingChange(padding))
            },
            cutoutPaddingApply = cutoutPaddingApply,
            onCutoutPaddingApplyChange = { isApply ->
                // 实现刘海边距切换
                viewModel.onEvent(ReaderEvent.OnCutoutPaddingApplyChange(isApply))
            },
            bottomBarPadding = bottomBarPadding,
            onBottomMarginChange = { margin ->
                // 实现底部边距变更
                viewModel.onEvent(ReaderEvent.OnBottomMarginChange(margin))
            },
            // 系统设置参数
            customBrightnessEnabled = isCustomBrightness,
            onCustomBrightnessEnabledChange = { isEnabled ->
                // 实现自定义亮度启用变更
                viewModel.onEvent(ReaderEvent.OnCustomBrightnessEnabledChange(isEnabled))
            },
            customBrightnessValue = customBrightnessValue,
            onCustomBrightnessValueChange = { value ->
                // 实现自定义亮度值变更
                viewModel.onEvent(ReaderEvent.OnCustomBrightnessValueChange(value))
            },
            screenOrientation = screenOrientation,
            onScreenOrientationChange = { orientation ->
                // 实现屏幕方向变更
                viewModel.onEvent(ReaderEvent.OnScreenOrientationChange(orientation))
            },
            // 杂项设置参数
            isFullScreen = isFullScreen,
            onIsFullScreenChange = { isFullScreen ->
                // 实现全屏模式变更
                viewModel.onEvent(ReaderEvent.OnIsFullScreenChange(isFullScreen))
            },
            isKeepScreenOn = screenAlive,
            onIsKeepScreenOnChange = { isKeep ->
                // 实现屏幕常亮变更
                viewModel.onEvent(ReaderEvent.OnIsKeepScreenOnChange(isKeep))
            },
            isHideBarWhenQuickScroll = isHideBarOnQuickScroll,
            onIsHideBarWhenQuickScrollChange = { isHide ->
                // 实现快速滚动时隐藏栏变更
                viewModel.onEvent(ReaderEvent.OnIsHideBarWhenQuickScrollChange(isHide))
            },

            // ReaderContent 参数
            fonts = fonts, // 字体列表
            fontId = fontId,
            onFontIdChange = { newFontId ->
                // 实现字体ID变更
                viewModel.onEvent(ReaderEvent.OnFontIdChange(newFontId))
            },
            fontSize = fontSize,
            onFontSizeChange = { newSize ->
                // 实现字体大小变更
                viewModel.onEvent(ReaderEvent.OnFontSizeChange(newSize))
            },
            fontWeight = fontWeight,
            onFontWeightChange = { newWeight ->
                // 实现字体粗细变更
                viewModel.onEvent(ReaderEvent.OnFontWeightChange(newWeight))
            },
            isReaderItalic = isItalic,
            onItalicChange = { isItalic ->
                // 实现斜体切换
                viewModel.onEvent(ReaderEvent.OnItalicChange(isItalic))
            },
            letterSpacing = letterSpacing.toInt(),
            onLetterSpacingChange = { spacing ->
                // 实现字间距变更
                viewModel.onEvent(ReaderEvent.OnLetterSpacingChange(spacing))
            },
            appTextAlign = appTextAlign,
            onTextAlignChange = { align ->
                // 实现文本对齐变更
                viewModel.onEvent(ReaderEvent.OnTextAlignChange(align))
            },
            lineHeight = lineHeight.toInt(),
            onLineHeightChange = { height ->
                // 实现行高变更
                viewModel.onEvent(ReaderEvent.OnLineHeightChange(height))
            },
            paragraphSpacing = paragraphSpacing,
            onParagraphSpacingChange = { spacing ->
                // 实现段落间距变更
                viewModel.onEvent(ReaderEvent.OnParagraphSpacingChange(spacing))
            },
            paragraphIndent = paragraphIndent,
            onParagraphIndentChange = { indent ->
                // 实现段落缩进变更
                viewModel.onEvent(ReaderEvent.OnParagraphIndentChange(indent))
            },
            imageVisible = imageVisible,
            onImageVisibleChange = { isVisible ->
                // 实现图片可见性切换
                viewModel.onEvent(ReaderEvent.OnImageVisibleChange(isVisible))
            },
            imageCaptionVisible = imageCaptionVisible,
            onImageCaptionVisibleChange = { isVisible ->
                // 实现图片说明可见性切换
                viewModel.onEvent(ReaderEvent.OnImageCaptionVisibleChange(isVisible))
            },
            imageColorEffect = imageColorEffect,
            onImageColorEffectChange = { effect ->
                // 实现图片颜色效果变更
                viewModel.onEvent(ReaderEvent.OnImageColorEffectChange(effect))
            },
            imageCornerRadius = imageCornerRadius,
            onImageCornerRadiusChange = { radius ->
                // 实现图片圆角变更
                viewModel.onEvent(ReaderEvent.OnImageCornerRadiusChange(radius))
            },
            imageAlign = imageAlign,
            onImageAlignChange = { align ->
                // 实现图片对齐变更
                viewModel.onEvent(ReaderEvent.OnImageAlignChange(align))
            },
            imageSizePercent = imageSizePercent.toFloat(),
            onImageSizePercentChange = { percent ->
                // 实现图片尺寸变更
                viewModel.onEvent(ReaderEvent.OnImageSizePercentChange(percent))
            },
            chapterTitleAlign = chapterTitleAlign,
            onChapterTitleAlignChange = { align ->
                // 实现章节标题对齐变更
                viewModel.onEvent(ReaderEvent.OnChapterTitleAlignChange(align))
            },
            bottomProgressTextVisible = isBottomProgressTextVisible,
            onProgressBarVisibleChange = { isVisible ->
                // 实现进度条可见性切换
                viewModel.onEvent(ReaderEvent.OnProgressBarVisibleChange(isVisible))
            },
            progressBarFontSize = progressTextSize,
            onProgressBarFontSizeChange = { size ->
                // 实现进度条字体大小变更
                viewModel.onEvent(ReaderEvent.OnProgressBarFontSizeChange(size))
            },
            progressBarMargin = bottomProgressPadding,
            onProgressBarMarginChange = { margin ->
                // 实现进度条边距变更
                viewModel.onEvent(ReaderEvent.OnProgressBarMarginChange(margin))
            },
            progressBarTextAlign = bottomProgressTextAlign,
            onProgressBarTextAlignChange = { align ->
                // 实现进度条文本对齐变更
                viewModel.onEvent(ReaderEvent.OnProgressBarTextAlignChange(align))
            },

            // ColorContent 参数
            colorPresets = colorPresets, // 从ViewModel获取颜色预设列表
            onColorPresetsChange = { presets ->
                // 实现颜色预设列表变更（拖拽排序）
                viewModel.onEvent(ReaderEvent.OnColorPresetDragStopped(presets))
            },
            onColorPresetNameChange = { preset, name ->
                // 实现颜色预设名称变更
                viewModel.onEvent(ReaderEvent.OnColorPresetNameChange(preset, name))
            },
            onColorPresetDelete = { preset ->
                // 实现颜色预设删除
                viewModel.onEvent(ReaderEvent.OnDeleteColorPresetClick(preset))
            },
            onColorPresetShuffle = { preset ->
                // 实现颜色预设打乱
                viewModel.onEvent(ReaderEvent.OnShuffleColorPresetClick(preset))
            },
            onColorPresetAdd = {
                // 实现颜色预设添加
                viewModel.onEvent(ReaderEvent.OnAddColorPresetClick)
            },
            onColorPresetBgColorChange = { preset, colorArgb ->
                // 实现背景颜色变更
                viewModel.onEvent(ReaderEvent.OnColorPresetBgColorChange(preset, colorArgb))
            },
            onColorPresetTextColorChange = { preset, colorArgb ->
                // 实现文本颜色变更
                viewModel.onEvent(ReaderEvent.OnColorPresetTextColorChange(preset, colorArgb))
            },
            isQuickColorPresetChangeEnabled = isQuickColorPresetChangeEnabled, // 从ViewModel获取
            onQuickColorPresetChangeEnabledChange = { enabled ->
                // 实现快速颜色预设切换
                viewModel.onEvent(ReaderEvent.OnQuickChangeColorPresetEnabledChange(enabled))
            }
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
    onReaderSettingsClick: () -> Unit,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    listState: LazyListState,
    // 字体设置
    fontFamily: FontFamily,
    fontSize: Int,
    fontWeight: AppFontWeight,
    isItalic: Boolean,
    letterSpacing: Int,
    // 文本设置
    appTextAlign: AppTextAlign,
    lineHeight: Int,
    paragraphSpacing: Int,
    paragraphIndent: Int,
    // 图片设置
    imageVisible: Boolean,
    imageCaptionVisible: Boolean,
    imageColorEffect: ImageColorEffect,
    imageCornerRadius: Int,
    imageAlign: AppImageAlign,
    imageSizePercent: Float,
    // 章节设置
    chapterTitleAlign: AppChapterTitleAlign,
    // 边距设置
    sidePadding: Int,
    verticalPadding: Int,
    cutoutPaddingApply: Boolean,
    fullScreen: Boolean,
    colorPreset: ColorPreset,
    presetChangeEnabled: Boolean,
    swipeLastColorPreset: () -> Unit,
    swipeNextColorPreset: () -> Unit,
    bottomBarPadding: Int,
    // 进度文本设置
    progressText: String,
    progressTextVisible: Boolean,
    progressTextFontSize: Int,
    progressTextPadding: Int,
    progressTextAlign: ProgressTextAlign,
    nestedScrollConnection: NestedScrollConnection,
    // 修改进度
    onProgressChange: (Float) -> Unit,
    readingMode: ReadingMode,
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
                contents = uiState.readerItems.filter { content ->
                    when (content) {
                        is ReaderContent.Title, is ReaderContent.Divider, is ReaderContent.Paragraph -> {
                            true
                        }

                        is ReaderContent.Image -> {
                            imageVisible
                        }

                        is ReaderContent.ImageDescription -> {
                            !(!imageVisible || !imageCaptionVisible)
                        }
                    }
                },
                listState = listState,
                onContentClick = onReadContentClick,
                colorPreset = colorPreset,
                // 字体设置
                fontFamily = fontFamily,
                fontSize = fontSize,
                fontWeight = fontWeight,
                isItalic = isItalic,
                letterSpacing = letterSpacing,
                // 文本设置
                appTextAlign = appTextAlign,
                lineHeight = lineHeight,
                paragraphSpacing = paragraphSpacing,
                paragraphIndent = paragraphIndent,
                // 图片设置
                imageVisible = imageVisible,
                imageCaptionVisible = imageCaptionVisible,
                imageColorEffect = imageColorEffect,
                imageCornerRadius = imageCornerRadius,
                imageAlign = imageAlign,
                imageSizePercent = imageSizePercent,
                // 章节设置
                chapterTitleAlign = chapterTitleAlign,
                // 边距设置
                sidePadding = sidePadding,
                verticalPadding = verticalPadding,
                cutoutPaddingApply = cutoutPaddingApply,
                isFullScreen = fullScreen,
                // 进度条设置
                progressText = progressText,
                progressTextVisible = progressTextVisible,
                progressTextFontSize = progressTextFontSize,
                progressTextPadding = progressTextPadding,
                progressTextAlign = progressTextAlign,
                // 底部进度条是否可见
                barsVisible = isBarsVisible,
                nestedScrollConnection = nestedScrollConnection,
                readingMode = readingMode
            )
        }

        // 顶部工具栏
        AnimatedVisibility(
            visible = isBarsVisible,
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
                chapterProgress = uiState.chapterProgress,
                onBackClick = onBackClick,
                onChapterMenuClick = onChapterMenuClick,
                onReaderSettingsClick = onReaderSettingsClick,
                onSwipeLeft = swipeNextColorPreset,
                onSwipeRight = swipeLastColorPreset,
                swipeEnabled = presetChangeEnabled,
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
                progress = uiState.progress.progressPercent,
                progressText = progressText,
                onProgressChange = onProgressChange,
                isPrevVisible = isPrevVisible,
                isNextVisible = isNextVisible,
                onPrevClick = onPrevClick,
                onNextClick = onNextClick,
                bottomBarPadding = bottomBarPadding,
            )
        }
    }
}