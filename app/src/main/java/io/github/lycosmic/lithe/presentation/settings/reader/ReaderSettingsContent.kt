package io.github.lycosmic.lithe.presentation.settings.reader

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import io.github.lycosmic.data.settings.AppImageAlign
import io.github.lycosmic.data.settings.AppTextAlign
import io.github.lycosmic.data.settings.ImageColorEffect
import io.github.lycosmic.data.settings.ReaderFontWeight
import io.github.lycosmic.domain.model.MyFontFamily
import io.github.lycosmic.lithe.R
import io.github.lycosmic.lithe.presentation.settings.components.SelectionChip
import io.github.lycosmic.lithe.presentation.settings.components.SettingsGroupTitle
import io.github.lycosmic.lithe.presentation.settings.components.SettingsItemWithFloatSlider
import io.github.lycosmic.lithe.presentation.settings.components.SettingsItemWithIntSlider
import io.github.lycosmic.lithe.presentation.settings.components.SettingsItemWithSwitch
import io.github.lycosmic.lithe.presentation.settings.components.SettingsSubGroupTitle
import io.github.lycosmic.lithe.ui.components.LitheSegmentedButton
import io.github.lycosmic.lithe.ui.components.OptionItem
import io.github.lycosmic.lithe.util.AppConstants
import io.github.lycosmic.lithe.util.AppConstants.CORNER_RADIUS_INT_RANGE
import io.github.lycosmic.lithe.util.extensions.displayNameResId
import io.github.lycosmic.lithe.util.extensions.labelResId


/**
 * 阅读器设置页面内容
 */
@Composable
fun ReaderSettingsContent(
    modifier: Modifier = Modifier,
    fontId: String,
    fontSize: Int,
    fontWeight: ReaderFontWeight,
    isReaderItalic: Boolean,
    letterSpacing: Int,
    appTextAlign: AppTextAlign,
    lineHeight: Int,
    paragraphSpacing: Int,
    paragraphIndent: Int,
    imageVisible: Boolean,
    imageCaptionVisible: Boolean,
    imageColorEffect: ImageColorEffect,
    imageCornerRadius: Int,
    imageAlign: AppImageAlign,
    imageSizePercent: Float,
    onFontIdChange: (String) -> Unit,
    onFontSizeChange: (Int) -> Unit,
    onFontWeightChange: (ReaderFontWeight) -> Unit,
    onItalicChange: (Boolean) -> Unit,
    onLetterSpacingChange: (Int) -> Unit,
    onTextAlignChange: (AppTextAlign) -> Unit,
    onLineHeightChange: (Int) -> Unit,
    onParagraphSpacingChange: (Int) -> Unit,
    onParagraphIndentChange: (Int) -> Unit,
    onImageVisibleChange: (Boolean) -> Unit,
    onImageCaptionVisibleChange: (Boolean) -> Unit,
    onImageColorEffectChange: (ImageColorEffect) -> Unit,
    onImageCornerRadiusChange: (Int) -> Unit,
    onImageAlignChange: (AppImageAlign) -> Unit,
    onImageSizePercentChange: (Float) -> Unit,
    fonts: List<MyFontFamily> = emptyList(),
    currentFontFamily: FontFamily,
) {
    LazyColumn(modifier) {
        item {
            Column {
                FontPreviewCard(
                    font = currentFontFamily,
                    weight = fontWeight,
                    fontSize = fontSize,
                    isItalic = isReaderItalic,
                    letterSpacing = letterSpacing,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    appTextAlign = appTextAlign,
                    lineHeight = lineHeight,
                    paragraphIndent = paragraphIndent,
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            }
        }

        item {
            SettingsGroupTitle(
                title = {
                    stringResource(id = R.string.reader_settings_font_group_title)
                },
                modifier = Modifier.padding(
                    top = 16.dp,
                    bottom = 8.dp,
                    start = 16.dp,
                    end = 16.dp
                )
            )
        }

        // 字体系列
        item {
            Column {
                SettingsSubGroupTitle(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                    title = stringResource(id = R.string.reader_settings_font_family)
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    fonts.forEach { fontFamily ->
                        val text = when {
                            (MyFontFamily.Default.id == fontFamily.id) -> {
                                stringResource(id = R.string.reader_settings_font_family_default)
                            }

                            else -> fontFamily.displayName
                        }

                        SelectionChip(
                            text = text,
                            isSelected = fontFamily.id == fontId,
                            onClick = {
                                onFontIdChange(fontFamily.id)
                            },
                            fontFamily = fontFamily.family as? FontFamily ?: FontFamily.Default
                        )
                    }
                }
            }
        }


        // 字体粗细
        item {
            Column {
                SettingsSubGroupTitle(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                    title = stringResource(id = R.string.reader_settings_font_weight)
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    ReaderFontWeight.entries.forEach { weight ->
                        SelectionChip(
                            text = stringResource(id = weight.displayNameResId),
                            isSelected = weight == fontWeight,
                            onClick = {
                                onFontWeightChange(weight)
                            },
                            fontWeight = weight.weight
                        )
                    }
                }
            }
        }

        // 字体样式
        item {
            Column {
                SettingsSubGroupTitle(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                    title = stringResource(id = R.string.reader_settings_font_style)
                )

                LitheSegmentedButton(
                    items = listOf(
                        OptionItem(
                            label = stringResource(R.string.reader_settings_font_style_normal),
                            value = false,
                            selected = !isReaderItalic
                        ),
                        OptionItem(
                            label = stringResource(R.string.reader_settings_font_style_italic),
                            value = true,
                            selected = isReaderItalic,
                            fontStyle = FontStyle.Italic
                        ),
                    ),
                    onClick = { isItalic ->
                        onItalicChange(isItalic)
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // 字体大小和字间距
        item {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // 字体大小
                SettingsItemWithIntSlider(
                    title = stringResource(id = R.string.reader_settings_font_size),
                    subtitleResId = R.string.reader_settings_font_size_format,
                    initialValue = fontSize,
                    onSave = onFontSizeChange,
                    valueRange = AppConstants.FONT_SIZE_INT_RANGE,
                )

                // 字间距
                SettingsItemWithIntSlider(
                    title = stringResource(id = R.string.reader_settings_font_letter_spacing),
                    subtitleResId =
                        R.string.reader_settings_font_letter_spacing_format,
                    initialValue = letterSpacing,
                    onSave = onLetterSpacingChange,
                    valueRange = AppConstants.LETTER_SPACING_INT_RANGE,
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
        }

        // --- 文本 ---
        item {
            SettingsGroupTitle(
                title = {
                    stringResource(id = R.string.reader_settings_text_group_title)
                },
                modifier = Modifier.padding(
                    top = 16.dp,
                    bottom = 8.dp,
                    start = 16.dp,
                    end = 16.dp
                )
            )
        }

        // 文本对齐
        item {
            Column {
                SettingsSubGroupTitle(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                    title = stringResource(id = R.string.reader_settings_text_align_title)
                )

                LitheSegmentedButton(
                    items = AppTextAlign.entries.map { textAlign ->
                        OptionItem(
                            label = stringResource(textAlign.labelResId),
                            value = textAlign,
                            selected = textAlign == appTextAlign
                        )
                    },
                    onClick = { textAlign ->
                        onTextAlignChange(textAlign)
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }


        // 行高和段落间距
        item {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // 行高
                SettingsItemWithIntSlider(
                    title = stringResource(id = R.string.reader_settings_line_height),
                    subtitleResId =
                        R.string.reader_settings_line_height_format,
                    initialValue = lineHeight,
                    onSave = onLineHeightChange,
                    valueRange = AppConstants.LINE_HEIGHT_INT_RANGE,
                )

                // 段落间距
                SettingsItemWithIntSlider(
                    title = stringResource(id = R.string.reader_settings_paragraph_spacing),
                    subtitleResId =
                        R.string.reader_settings_paragraph_spacing_format,
                    initialValue = paragraphSpacing,
                    onSave = onParagraphSpacingChange,
                    valueRange = AppConstants.PARAGRAPH_SPACING_INT_RANGE,
                )

                // 段落缩进
                AnimatedVisibility(
                    visible = appTextAlign == AppTextAlign.START || appTextAlign == AppTextAlign.JUSTIFY,
                    enter = slideInVertically(),
                    exit = slideOutVertically()
                ) {

                    SettingsItemWithIntSlider(
                        title = stringResource(id = R.string.reader_settings_paragraph_indent),
                        subtitleResId =
                            R.string.reader_settings_paragraph_indent_format,
                        initialValue = paragraphIndent,
                        onSave = onParagraphIndentChange,
                        valueRange = AppConstants.PARAGRAPH_INDENT_INT_RANGE,
                    )
                }
            }
        }



        item {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
        }

        // --- 图片 ---
        item {
            SettingsGroupTitle(
                title = {
                    stringResource(id = R.string.reader_settings_image_group_title)
                },
                modifier = Modifier.padding(
                    top = 16.dp,
                    bottom = 8.dp,
                    start = 16.dp,
                    end = 16.dp
                )
            )
        }


        item {
            // 图片显示
            SettingsItemWithSwitch(
                title = stringResource(R.string.reader_settings_image_show_title),
                subtitle = stringResource(R.string.reader_settings_image_show_subtitle),
                checked = imageVisible,
                onCheckedChange = onImageVisibleChange
            )
        }

        item {
            AnimatedVisibility(
                visible = imageVisible,
                enter = slideInVertically(),
                exit = slideOutVertically()
            ) {
                Column {
                    // 图片说明文字
                    SettingsItemWithSwitch(
                        title = stringResource(R.string.reader_settings_image_caption_show_title),
                        subtitle = stringResource(R.string.reader_settings_image_caption_show_subtitle),
                        checked = imageCaptionVisible,
                        onCheckedChange = onImageCaptionVisibleChange
                    )

                    // 图片颜色效果
                    Column {
                        SettingsSubGroupTitle(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                            title = stringResource(id = R.string.reader_settings_image_color_effect_title)
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            ImageColorEffect.entries.forEach { effect ->
                                SelectionChip(
                                    text = stringResource(effect.labelResId),
                                    isSelected = effect == imageColorEffect,
                                    onClick = {
                                        onImageColorEffectChange(effect)
                                    },
                                )
                            }
                        }
                    }

                    // 边角圆度
                    SettingsItemWithIntSlider(
                        title = stringResource(R.string.reader_settings_image_corner_radius),
                        subtitleResId =
                            R.string.reader_settings_image_corner_radius_format,
                        initialValue = imageCornerRadius,
                        onSave = onImageCornerRadiusChange,
                        valueRange = CORNER_RADIUS_INT_RANGE,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // 图片对齐
                    Column {
                        SettingsSubGroupTitle(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                            title = stringResource(id = R.string.reader_settings_image_align_title)
                        )

                        LitheSegmentedButton(
                            items = AppImageAlign.entries.map { align ->
                                OptionItem(
                                    label = stringResource(align.labelResId),
                                    value = align,
                                    selected = align == imageAlign
                                )
                            },
                            onClick = onImageAlignChange,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    // 图片尺寸比例
                    SettingsItemWithFloatSlider(
                        title = stringResource(id = R.string.reader_settings_image_size_percent),
                        subtitleResId =
                            R.string.reader_settings_image_size_percent_format,
                        initialValue = imageSizePercent,
                        onSave = onImageSizePercentChange,
                        floatRange = AppConstants.IMAGE_SIZE_RATIO_INT_RANGE.first.toFloat()
                            .rangeTo(AppConstants.IMAGE_SIZE_RATIO_INT_RANGE.last.toFloat()),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
        }

        // --- 章节 ---


        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

