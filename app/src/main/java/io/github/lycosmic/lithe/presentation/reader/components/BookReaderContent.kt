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
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import io.github.lycosmic.data.settings.AppChapterTitleAlign
import io.github.lycosmic.data.settings.AppImageAlign
import io.github.lycosmic.data.settings.AppTextAlign
import io.github.lycosmic.data.settings.ImageColorEffect
import io.github.lycosmic.data.settings.ProgressTextAlign
import io.github.lycosmic.data.util.extensions.weight
import io.github.lycosmic.domain.model.AppFontWeight
import io.github.lycosmic.domain.model.ColorPreset
import io.github.lycosmic.lithe.presentation.reader.ReaderContent
import io.github.lycosmic.lithe.ui.components.StyledText
import io.github.lycosmic.lithe.util.clickableNoRipple
import io.github.lycosmic.lithe.util.extensions.backgroundColor
import io.github.lycosmic.lithe.util.extensions.textColor

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
) {
    val density = LocalDensity.current

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

    SelectionContainer {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(animatedBgColor)
                .clickableNoRipple {
                    onContentClick()
                }
                .padding(contentPadding)
                .padding(vertical = verticalPadding)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.Top,
                state = listState,
                contentPadding = PaddingValues(
                    top = (WindowInsets.displayCutout.asPaddingValues()
                        .calculateTopPadding() + paragraphSpacing)
                        .coerceAtLeast(18.dp),
                    bottom = (WindowInsets.displayCutout.asPaddingValues()
                        .calculateBottomPadding() + paragraphSpacing)
                        .coerceAtLeast(18.dp),
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(nestedScrollConnection)
            ) {
                itemsIndexed(
                    items = contents,
                    key = { index, _ -> index }) { index, content ->
                    // 段落间距
                    if (index > 0) {
                        Spacer(modifier = Modifier.height(paragraphSpacing))
                    }

                    when (content) {
                        is ReaderContent.Title -> {
                            // 标题
                            Column(
                                Modifier
                                    .animateItem(
                                        fadeInSpec = null,
                                        fadeOutSpec = null
                                    )
                                    .fillMaxWidth()
                            ) {
                                Spacer(modifier = Modifier.height(22.dp))

                                StyledText(
                                    text = buildAnnotatedString { append(content.text) },
                                    modifier = Modifier
                                        .padding(horizontal = computedSidePadding)
                                        .fillMaxWidth(),
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        color = animatedTextColor,
                                        textAlign = titleAlign
                                    )
                                )
                            }
                        }

                        is ReaderContent.Paragraph -> {
                            // 段落
                            Column(
                                modifier = Modifier
                                    .animateItem(fadeInSpec = null, fadeOutSpec = null)
                                    .fillMaxWidth()
                                    .padding(horizontal = computedSidePadding),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = paragraphHorizontalAlignment
                            ) {
                                StyledText(
                                    text = content.text,
                                    style = TextStyle(
                                        fontFamily = fontFamily,
                                        fontWeight = paragraphFontWeight,
                                        textAlign = paragraphTextAlign,
                                        textIndent = TextIndent(firstLine = paragraphIndent),
                                        fontStyle = paragraphFontStyle,
                                        letterSpacing = paragraphLetterSpacing,
                                        fontSize = fontSize.sp,
                                        lineHeight = paragraphLineHeight,
                                        color = animatedTextColor,
                                        lineBreak = LineBreak.Paragraph
                                    )
                                )
                            }
                        }

                        is ReaderContent.Image -> {
                            if (!imageVisible) {
                                return@itemsIndexed
                            }

                            // 图片
                            Box(
                                modifier = Modifier
                                    .animateItem(
                                        fadeInSpec = null,
                                        fadeOutSpec = null
                                    )
                                    .padding(horizontal = computedSidePadding)
                                    .fillMaxWidth(),
                                contentAlignment = imageAlign
                            ) {
                                AsyncImage(
                                    model = content.path,
                                    contentDescription = null,
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(imageCornerRadius))
                                        .fillMaxWidth(imageWidthPercent),
                                    colorFilter = imageColorEffects,
                                )
                            }
                        }

                        is ReaderContent.Divider -> {
                            // 分割线
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 16.dp),
                                color = animatedTextColor.copy(alpha = 0.4f)
                            )
                        }

                        is ReaderContent.ImageDescription -> {
                            if (!imageVisible || !imageCaptionVisible) {
                                return@itemsIndexed
                            }


                            // 图片描述
                            Column(
                                modifier = Modifier
                                    .animateItem(fadeInSpec = null, fadeOutSpec = null)
                                    .fillMaxWidth()
                                    .padding(horizontal = computedSidePadding),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = paragraphHorizontalAlignment
                            ) {
                                StyledText(
                                    text = content.caption,
                                    style = TextStyle(
                                        fontFamily = fontFamily,
                                        fontWeight = paragraphFontWeight,
                                        textAlign = paragraphTextAlign,
                                        textIndent = TextIndent(firstLine = paragraphIndent),
                                        fontStyle = paragraphFontStyle,
                                        letterSpacing = paragraphLetterSpacing,
                                        fontSize = fontSize.sp,
                                        lineHeight = paragraphLineHeight,
                                        color = animatedTextColor,
                                        lineBreak = LineBreak.Paragraph
                                    )
                                )
                            }
                        }
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