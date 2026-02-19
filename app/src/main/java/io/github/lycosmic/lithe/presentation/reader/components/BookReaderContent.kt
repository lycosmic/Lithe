package io.github.lycosmic.lithe.presentation.reader.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsIgnoringVisibility
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import io.github.lycosmic.data.settings.AppChapterTitleAlign
import io.github.lycosmic.data.settings.AppImageAlign
import io.github.lycosmic.data.settings.AppTextAlign
import io.github.lycosmic.data.settings.ImageColorEffect
import io.github.lycosmic.data.settings.ProgressTextAlign
import io.github.lycosmic.data.settings.ReadingMode
import io.github.lycosmic.data.util.extensions.weight
import io.github.lycosmic.domain.model.AppFontWeight
import io.github.lycosmic.domain.model.ColorPreset
import io.github.lycosmic.lithe.log.logD
import io.github.lycosmic.lithe.presentation.reader.ReaderContent
import io.github.lycosmic.lithe.presentation.reader.model.ReaderStyle
import io.github.lycosmic.lithe.presentation.reader.pager.BookPage
import io.github.lycosmic.lithe.presentation.reader.pager.PrecisePaginator
import io.github.lycosmic.lithe.ui.components.CircularWavyProgressIndicator
import io.github.lycosmic.lithe.ui.components.StyledText
import io.github.lycosmic.lithe.util.clickableNoRipple
import io.github.lycosmic.lithe.util.extensions.backgroundColor
import io.github.lycosmic.lithe.util.extensions.textColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * 阅读内容
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BookReaderContent(
    contents: List<ReaderContent>,
    onContentClick: () -> Unit,
    listState: LazyListState,
    // 颜色预设
    colorPreset: ColorPreset,
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
    chapterTitleAlign: AppChapterTitleAlign,
    // 边距设置
    sidePadding: Int,
    verticalPadding: Int,
    cutoutPaddingApply: Boolean,
    isFullScreen: Boolean,
    // 进度条设置
    progressText: String,
    progressTextVisible: Boolean,
    progressTextFontSize: Int,
    progressTextPadding: Int,
    progressTextAlign: ProgressTextAlign,
    barsVisible: Boolean,
    nestedScrollConnection: NestedScrollConnection,
    readingMode: ReadingMode,
) {
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    // 侧边距
    val computedSidePadding = remember(sidePadding) {
        (sidePadding * 3).dp
    }

    val animSpec = remember { tween<Color>(durationMillis = 500) }

    // 背景色
    val animatedBgColor by animateColorAsState(
        targetValue = colorPreset.backgroundColor,
        animationSpec = animSpec,
        label = "bgColor"
    )
    // 文字颜色
    val animatedTextColor by animateColorAsState(
        targetValue = colorPreset.textColor,
        animationSpec = animSpec,
        label = "textColor"
    )

    // 标题文本对齐
    val titleAlign = remember(chapterTitleAlign) {
        when (chapterTitleAlign) {
            AppChapterTitleAlign.START -> TextAlign.Start
            AppChapterTitleAlign.JUSTIFY -> TextAlign.Justify
            AppChapterTitleAlign.CENTER -> TextAlign.Center
            AppChapterTitleAlign.END -> TextAlign.End
        }
    }

    // 图片对齐
    val imageAlign = remember(imageAlign) {
        when (imageAlign) {
            AppImageAlign.START -> Alignment.CenterStart
            AppImageAlign.CENTER -> Alignment.Center
            AppImageAlign.END -> Alignment.CenterEnd
        }
    }

    // 图片宽度
    val imageWidthPercent = remember(imageSizePercent) {
        (imageSizePercent / 100f).coerceAtLeast(minimumValue = 0.01f)
    }

    // 图片圆角
    val imageCornerRadius = remember(imageCornerRadius, imageWidthPercent) {
        (imageCornerRadius * 3 * imageWidthPercent).dp
    }

    // 图片颜色效果
    val imageColorEffects = remember(
        imageColorEffect,
        colorPreset.backgroundColor,
        colorPreset.textColor
    ) {
        when (imageColorEffect) {
            ImageColorEffect.NONE -> null
            ImageColorEffect.GRAYSCALE -> ColorFilter.colorMatrix(
                ColorMatrix().apply { setToSaturation(0f) }
            )

            ImageColorEffect.FONT_COLOR -> ColorFilter.tint(
                color = colorPreset.textColor,
                blendMode = BlendMode.Color
            )

            ImageColorEffect.BACKGROUND_COLOR -> ColorFilter.tint(
                color = colorPreset.backgroundColor,
                blendMode = BlendMode.Color
            )
        }
    }

    // 段落水平方式对齐
    val paragraphHorizontalAlignment = remember(appTextAlign) {
        when (appTextAlign) {
            AppTextAlign.START, AppTextAlign.JUSTIFY -> Alignment.Start
            AppTextAlign.CENTER -> Alignment.CenterHorizontally
            AppTextAlign.END -> Alignment.End
        }
    }

    // 段落文本对齐
    val paragraphTextAlign = remember(appTextAlign) {
        AppTextAlign.getTextAlign(appTextAlign)
    }

    // 段落文本粗细
    val paragraphFontWeight = remember(fontWeight) {
        fontWeight.weight
    }

    // 段落缩进
    val paragraphIndent = remember(paragraphIndent, appTextAlign) {
        when (appTextAlign) {
            AppTextAlign.START, AppTextAlign.JUSTIFY -> (paragraphIndent * 6).sp
            AppTextAlign.CENTER, AppTextAlign.END -> 0.sp
        }
    }

    // 段落字体样式
    val paragraphFontStyle = remember(isItalic) {
        if (isItalic) FontStyle.Italic else FontStyle.Normal
    }

    // 段落字间距
    val paragraphLetterSpacing = remember(letterSpacing) {
        (letterSpacing / 100f).em
    }

    // 段落行高
    val paragraphLineHeight = remember(fontSize, lineHeight) {
        (fontSize + lineHeight).sp
    }

    // 段落间距
    val paragraphSpacing = remember(paragraphSpacing, lineHeight) {
        (paragraphSpacing * 3).dp.coerceAtLeast(
            with(density) {
                lineHeight.sp.toDp().value * 0.5f
            }.dp
        )
    }


    // 刘海屏边距
    val layoutDirection = LocalLayoutDirection.current
    val cutoutInsets = WindowInsets.displayCutout
    val cutoutInsetsPadding = remember(cutoutPaddingApply) {
        derivedStateOf {
            cutoutInsets.asPaddingValues(density = density).run {
                if (cutoutPaddingApply) PaddingValues(
                    top = calculateTopPadding(),
                    start = calculateStartPadding(layoutDirection),
                    end = calculateEndPadding(layoutDirection),
                    bottom = calculateBottomPadding()
                ) else PaddingValues(0.dp)
            }
        }
    }

    // 系统栏边距
    val systemBarsInsets = WindowInsets.systemBarsIgnoringVisibility
    val systemBarsInsetsPadding = remember(isFullScreen) {
        derivedStateOf {
            systemBarsInsets.asPaddingValues(density = density).run {
                if (!isFullScreen) PaddingValues(
                    top = calculateTopPadding(),
                    start = calculateStartPadding(layoutDirection),
                    end = calculateEndPadding(layoutDirection),
                    bottom = calculateBottomPadding()
                ) else PaddingValues(0.dp)
            }
        }
    }

    // 内容边距
    val contentPadding = remember(
        cutoutInsetsPadding.value,
        systemBarsInsetsPadding.value
    ) {
        PaddingValues(
            top = systemBarsInsetsPadding.value.calculateTopPadding().run {
                if (equals(0.dp)) return@run cutoutInsetsPadding.value
                    .calculateTopPadding()
                this
            },
            start = systemBarsInsetsPadding.value.calculateStartPadding(layoutDirection).run {
                if (equals(0.dp)) return@run cutoutInsetsPadding.value
                    .calculateStartPadding(layoutDirection)
                this
            },
            end = systemBarsInsetsPadding.value.calculateEndPadding(layoutDirection).run {
                if (equals(0.dp)) return@run cutoutInsetsPadding.value
                    .calculateEndPadding(layoutDirection)
                this
            },
            bottom = systemBarsInsetsPadding.value.calculateBottomPadding().run {
                if (equals(0.dp)) return@run cutoutInsetsPadding.value
                    .calculateBottomPadding()
                this
            }
        )
    }

    // 垂直间距
    val verticalPadding = remember(verticalPadding) {
        (verticalPadding * 4.5f).dp
    }

    // 进度文本边距
    val progressTextPadding = remember(progressTextPadding) {
        (progressTextPadding * 3).dp
    }
    // 进度文本字体大小
    val progressTextFontSize = remember(progressTextFontSize) {
        (progressTextFontSize * 2).sp
    }

    // 进度文本水平对齐
    val progressTextAlign = remember(progressTextAlign) {
        when (progressTextAlign) {
            ProgressTextAlign.START -> Alignment.CenterStart
            ProgressTextAlign.CENTER -> Alignment.Center
            ProgressTextAlign.END -> Alignment.CenterEnd
        }
    }


    // 分页数据
    var pages by remember {
        mutableStateOf<List<BookPage>>(emptyList())
    }

    // 分页状态
    val pagerState = rememberPagerState(
        initialPage = 0, // TODO: 根据阅读进度恢复
        pageCount = { pages.size }
    )

    val defaultScope = rememberCoroutineScope {
        Dispatchers.Default
    }

    val readerStyle = remember(
        animatedBgColor,
        animatedTextColor,
        fontFamily,
        fontSize,
        paragraphFontWeight,
        paragraphFontStyle,
        paragraphLineHeight,
        paragraphLetterSpacing,
        paragraphIndent,
        paragraphSpacing,
        titleAlign,
        paragraphTextAlign,
        paragraphHorizontalAlignment,
        imageVisible,
        imageCaptionVisible,
        imageAlign,
        imageWidthPercent,
        imageCornerRadius,
        imageColorEffects,
        computedSidePadding
    ) {
        ReaderStyle(
            // 核心颜色
            backgroundColor = animatedBgColor,
            textColor = animatedTextColor,

            // 字体排版
            fontFamily = fontFamily,
            fontSize = fontSize.sp,
            fontWeight = paragraphFontWeight,
            fontStyle = paragraphFontStyle,
            lineHeight = paragraphLineHeight,
            letterSpacing = paragraphLetterSpacing,
            paragraphIndent = paragraphIndent,
            paragraphSpacing = paragraphSpacing,

            // 对齐方式
            titleAlign = titleAlign,
            paragraphAlign = paragraphTextAlign,
            paragraphHorizontalAlign = paragraphHorizontalAlignment,

            // 图片设置
            imageVisible = imageVisible,
            imageCaptionVisible = imageCaptionVisible,
            imageAlign = imageAlign,
            imageWidthPercent = imageWidthPercent,
            imageCornerRadius = imageCornerRadius,
            imageColorFilter = imageColorEffects,

            // 边距
            sidePadding = computedSidePadding
        )
    }

    var availableW: Int
    var availableH: Int


    SelectionContainer {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .background(animatedBgColor)
                .padding(contentPadding)
                .padding(vertical = verticalPadding)
        ) {
            val screenWidth = constraints.maxWidth
            val screenHeight = constraints.maxHeight

            with(density) {
                // 获取左右 Padding 像素
                val paddingPx = computedSidePadding.toPx()

                // 获取上下 Padding 像素 (包括系统栏 Insets 和自定义垂直边距)
                val topPaddingPx = contentPadding.calculateTopPadding().toPx()
                val bottomPaddingPx = contentPadding.calculateBottomPadding().toPx()
                val vertPaddingPx = verticalPadding.toPx()

                // 计算内容可用区域，需要减去边距
                availableW = screenWidth - (paddingPx * 2).toInt()
                availableH =
                    screenHeight - (topPaddingPx + bottomPaddingPx + vertPaddingPx * 2).toInt()

                logD {
                    "availableW: $availableW, availableH: $availableH"
                }
            }

            LaunchedEffect(contents, readerStyle, availableW, availableH, readingMode) {
                // 计算分页结果
                defaultScope.launch {
                    if (readingMode != ReadingMode.SCROLL && availableW > 0 && availableH > 0) {
                        val calculatedPages = PrecisePaginator.paginate(
                            contents = contents,
                            readerStyle = readerStyle,
                            textMeasurer = textMeasurer,
                            density = density,
                            availableWidth = availableW,
                            availableHeight = availableH
                        )
                        pages = calculatedPages
                    }
                }
            }

            when (readingMode) {
                ReadingMode.SCROLL -> {
                    // === 垂直滚动模式 ===
                    LazyColumn(
                        verticalArrangement = Arrangement.Top,
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .clickableNoRipple {
                                onContentClick()
                            }
                            .nestedScroll(nestedScrollConnection)
                    ) {
                        itemsIndexed(
                            items = contents,
                            key = { index, _ -> index }) { index, content ->
                            // 段落间距
                            if (index > 0) {
                                Spacer(modifier = Modifier.height(paragraphSpacing))
                            }

                            // 核心渲染
                            ReaderItemRenderer(
                                content = content,
                                style = readerStyle,
                                modifier = Modifier.animateItem(
                                    fadeInSpec = null,
                                    fadeOutSpec = null
                                )
                            )
                        }
                    }
                }

                ReadingMode.SLIDE, ReadingMode.NONE -> {
                    // === 水平翻页 ===
                    if (pages.isNotEmpty()) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize(),
                            // 页面间距
                            pageSpacing = 0.dp,
                            // 允许用户手势滑动
                            userScrollEnabled = true,
                            verticalAlignment = Alignment.Top,
                        ) { index ->
                            pages.getOrNull(index)?.let { page ->
                                PageRenderer(
                                    page = page,
                                    readerStyle = readerStyle,
                                    onContentClick = {
                                        onContentClick()
                                    },
                                    onPageChange = { pageIndex ->
                                        if (pageIndex == page.index) {
                                            return@PageRenderer
                                        }

                                        defaultScope.launch {
                                            when (readingMode) {
                                                ReadingMode.SLIDE -> {
                                                    // 平滑切换
                                                    pagerState.animateScrollToPage(pageIndex)
                                                }

                                                ReadingMode.NONE -> {
                                                    // 无动画切换
                                                    pagerState.scrollToPage(pageIndex)
                                                }

                                                else -> {
                                                    throw IllegalStateException("Unreachable code")
                                                }
                                            }
                                        }

                                    },
                                    pagerState = pagerState
                                )
                            }
                        }
                    } else {
                        CircularWavyProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
            }


            // 底部进度条
            AnimatedVisibility(
                modifier = Modifier.align(Alignment.BottomCenter),
                visible = progressTextVisible && !barsVisible,
                enter = slideInVertically { it } + expandVertically(),
                exit = slideOutVertically { it } + shrinkVertically()
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(animatedBgColor)
                        .padding(
                            horizontal = computedSidePadding,
                            vertical = progressTextPadding
                        ),
                    contentAlignment = progressTextAlign
                ) {
                    DisableSelection {
                        StyledText(
                            text = progressText,
                            style = LocalTextStyle.current.copy(
                                color = animatedTextColor,
                                fontSize = progressTextFontSize
                            ),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }

}