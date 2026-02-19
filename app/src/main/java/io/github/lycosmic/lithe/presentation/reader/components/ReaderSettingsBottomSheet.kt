package io.github.lycosmic.lithe.presentation.reader.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.lycosmic.data.settings.AppChapterTitleAlign
import io.github.lycosmic.data.settings.AppImageAlign
import io.github.lycosmic.data.settings.AppTextAlign
import io.github.lycosmic.data.settings.ImageColorEffect
import io.github.lycosmic.data.settings.ProgressTextAlign
import io.github.lycosmic.data.settings.ReadingMode
import io.github.lycosmic.data.settings.ScreenOrientation
import io.github.lycosmic.data.util.extensions.weight
import io.github.lycosmic.domain.model.AppFontFamily
import io.github.lycosmic.domain.model.AppFontWeight
import io.github.lycosmic.domain.model.ColorPreset
import io.github.lycosmic.lithe.R
import io.github.lycosmic.lithe.presentation.settings.appearance.ColorPresetOption
import io.github.lycosmic.lithe.presentation.settings.appearance.components.ColorPresetEditorCard
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
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

/**
 * 阅读设置底部抽屉标签
 */
enum class ReaderSettingsTabType {
    // 常规
    GENERAL,

    // 阅读器
    READER,

    // 颜色
    COLOR,
}

val ReaderSettingsTabType.labelResId
    get() = when (this) {
        ReaderSettingsTabType.GENERAL -> R.string.reader_settings_tab_general
        ReaderSettingsTabType.READER -> R.string.reader_settings_tab_reader
        ReaderSettingsTabType.COLOR -> R.string.reader_settings_tab_color
    }


/**
 * 阅读设置底部抽屉
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderSettingsBottomSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    // 抽屉标签
    currentTab: ReaderSettingsTabType,
    onTabChange: (ReaderSettingsTabType) -> Unit,
    // 当前颜色预设
    currentColorPreset: ColorPreset,
    onColorPresetChange: (ColorPreset) -> Unit,
    // GeneralContent 参数
    pageTurnMode: ReadingMode,
    onPageTurnModeChange: (ReadingMode) -> Unit,
    sidePadding: Int,
    onSidePaddingChange: (Int) -> Unit,
    verticalPadding: Int,
    onVerticalPaddingChange: (Int) -> Unit,
    cutoutPaddingApply: Boolean,
    onCutoutPaddingApplyChange: (Boolean) -> Unit,
    bottomBarPadding: Int,
    onBottomMarginChange: (Int) -> Unit,
    // 系统设置参数
    customBrightnessEnabled: Boolean,
    onCustomBrightnessEnabledChange: (Boolean) -> Unit,
    customBrightnessValue: Float,
    onCustomBrightnessValueChange: (Float) -> Unit,
    screenOrientation: ScreenOrientation,
    onScreenOrientationChange: (ScreenOrientation) -> Unit,
    // 杂项设置参数
    isFullScreen: Boolean,
    onIsFullScreenChange: (Boolean) -> Unit,
    isKeepScreenOn: Boolean,
    onIsKeepScreenOnChange: (Boolean) -> Unit,
    isHideBarWhenQuickScroll: Boolean,
    onIsHideBarWhenQuickScrollChange: (Boolean) -> Unit,
    // ReaderContent 参数
    fonts: List<AppFontFamily>,
    fontId: String,
    onFontIdChange: (String) -> Unit,
    fontSize: Int,
    onFontSizeChange: (Int) -> Unit,
    fontWeight: AppFontWeight,
    onFontWeightChange: (AppFontWeight) -> Unit,
    isReaderItalic: Boolean,
    onItalicChange: (Boolean) -> Unit,
    letterSpacing: Int,
    onLetterSpacingChange: (Int) -> Unit,
    appTextAlign: AppTextAlign,
    onTextAlignChange: (AppTextAlign) -> Unit,
    lineHeight: Int,
    onLineHeightChange: (Int) -> Unit,
    paragraphSpacing: Int,
    onParagraphSpacingChange: (Int) -> Unit,
    paragraphIndent: Int,
    onParagraphIndentChange: (Int) -> Unit,
    imageVisible: Boolean,
    onImageVisibleChange: (Boolean) -> Unit,
    imageCaptionVisible: Boolean,
    onImageCaptionVisibleChange: (Boolean) -> Unit,
    imageColorEffect: ImageColorEffect,
    onImageColorEffectChange: (ImageColorEffect) -> Unit,
    imageCornerRadius: Int,
    onImageCornerRadiusChange: (Int) -> Unit,
    imageAlign: AppImageAlign,
    onImageAlignChange: (AppImageAlign) -> Unit,
    imageSizePercent: Float,
    onImageSizePercentChange: (Float) -> Unit,
    chapterTitleAlign: AppChapterTitleAlign,
    onChapterTitleAlignChange: (AppChapterTitleAlign) -> Unit,
    bottomProgressTextVisible: Boolean,
    onProgressBarVisibleChange: (Boolean) -> Unit,
    progressBarFontSize: Int,
    onProgressBarFontSizeChange: (Int) -> Unit,
    progressBarMargin: Int,
    onProgressBarMarginChange: (Int) -> Unit,
    progressBarTextAlign: ProgressTextAlign,
    onProgressBarTextAlignChange: (ProgressTextAlign) -> Unit,
    // ColorContent 参数
    colorPresets: List<ColorPreset>,
    onColorPresetsChange: (List<ColorPreset>) -> Unit,
    onColorPresetNameChange: (ColorPreset, String) -> Unit,
    onColorPresetDelete: (ColorPreset) -> Unit,
    onColorPresetShuffle: (ColorPreset) -> Unit,
    onColorPresetAdd: () -> Unit,
    onColorPresetBgColorChange: (ColorPreset, Int) -> Unit,
    onColorPresetTextColorChange: (ColorPreset, Int) -> Unit,
    isQuickColorPresetChangeEnabled: Boolean,
    onQuickColorPresetChangeEnabledChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    if (visible) {
        ModalBottomSheet(
            dragHandle = null,
            onDismissRequest = onDismissRequest,
            modifier = modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ) {
            Column(modifier = Modifier.padding(bottom = 12.dp)) {

                PrimaryTabRow(
                    selectedTabIndex = currentTab.ordinal,
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                    contentColor = MaterialTheme.colorScheme.primary,
                    divider = { HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant) }
                ) {
                    ReaderSettingsTabType.entries.forEach { tab ->
                        Tab(
                            selected = currentTab == tab,
                            onClick = { onTabChange(tab) },
                            text = {
                                Text(
                                    text = stringResource(id = tab.labelResId),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = if (currentTab == tab) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    when (currentTab) {
                        ReaderSettingsTabType.GENERAL -> {
                            GeneralContent(
                                pageTurnMode = pageTurnMode,
                                onPageTurnModeChange = onPageTurnModeChange,
                                sidePadding = sidePadding,
                                onSidePaddingChange = onSidePaddingChange,
                                verticalPadding = verticalPadding,
                                onVerticalPaddingChange = onVerticalPaddingChange,
                                cutoutPaddingApply = cutoutPaddingApply,
                                onCutoutPaddingApplyChange = onCutoutPaddingApplyChange,
                                bottomMargin = bottomBarPadding,
                                onBottomMarginChange = onBottomMarginChange,
                                // 系统设置参数
                                customBrightnessEnabled = customBrightnessEnabled,
                                onCustomBrightnessEnabledChange = onCustomBrightnessEnabledChange,
                                customBrightnessValue = customBrightnessValue,
                                onCustomBrightnessValueChange = onCustomBrightnessValueChange,
                                screenOrientation = screenOrientation,
                                onScreenOrientationChange = onScreenOrientationChange,
                                // 杂项设置参数
                                isFullScreen = isFullScreen,
                                onIsFullScreenChange = onIsFullScreenChange,
                                isKeepScreenOn = isKeepScreenOn,
                                onIsKeepScreenOnChange = onIsKeepScreenOnChange,
                                isHideBarWhenQuickScroll = isHideBarWhenQuickScroll,
                                onIsHideBarWhenQuickScrollChange = onIsHideBarWhenQuickScrollChange
                            )
                        }

                        ReaderSettingsTabType.READER -> {
                            ReaderContent(
                                fonts = fonts,
                                fontId = fontId,
                                onFontIdChange = onFontIdChange,
                                fontSize = fontSize,
                                onFontSizeChange = onFontSizeChange,
                                fontWeight = fontWeight,
                                onFontWeightChange = onFontWeightChange,
                                isReaderItalic = isReaderItalic,
                                onItalicChange = onItalicChange,
                                letterSpacing = letterSpacing,
                                onLetterSpacingChange = onLetterSpacingChange,
                                appTextAlign = appTextAlign,
                                onTextAlignChange = onTextAlignChange,
                                lineHeight = lineHeight,
                                onLineHeightChange = onLineHeightChange,
                                paragraphSpacing = paragraphSpacing,
                                onParagraphSpacingChange = onParagraphSpacingChange,
                                paragraphIndent = paragraphIndent,
                                onParagraphIndentChange = onParagraphIndentChange,
                                imageVisible = imageVisible,
                                onImageVisibleChange = onImageVisibleChange,
                                imageCaptionVisible = imageCaptionVisible,
                                onImageCaptionVisibleChange = onImageCaptionVisibleChange,
                                imageColorEffect = imageColorEffect,
                                onImageColorEffectChange = onImageColorEffectChange,
                                imageCornerRadius = imageCornerRadius,
                                onImageCornerRadiusChange = onImageCornerRadiusChange,
                                imageAlign = imageAlign,
                                onImageAlignChange = onImageAlignChange,
                                imageSizePercent = imageSizePercent,
                                onImageSizePercentChange = onImageSizePercentChange,
                                chapterTitleAlign = chapterTitleAlign,
                                onChapterTitleAlignChange = onChapterTitleAlignChange,
                                bottomProgressTextVisible = bottomProgressTextVisible,
                                onProgressBarVisibleChange = onProgressBarVisibleChange,
                                progressBarFontSize = progressBarFontSize,
                                onProgressBarFontSizeChange = onProgressBarFontSizeChange,
                                progressBarMargin = progressBarMargin,
                                onProgressBarMarginChange = onProgressBarMarginChange,
                                progressBarTextAlign = progressBarTextAlign,
                                onProgressBarTextAlignChange = onProgressBarTextAlignChange
                            )
                        }

                        ReaderSettingsTabType.COLOR -> {
                            ColorContent(
                                colorPresets = colorPresets,
                                currentColorPreset = currentColorPreset,
                                onCurrentColorPresetChange = onColorPresetChange,
                                onColorPresetsChange = onColorPresetsChange,
                                onColorPresetNameChange = onColorPresetNameChange,
                                onColorPresetDelete = onColorPresetDelete,
                                onColorPresetShuffle = onColorPresetShuffle,
                                onColorPresetAdd = onColorPresetAdd,
                                onColorPresetBgColorChange = onColorPresetBgColorChange,
                                onColorPresetTextColorChange = onColorPresetTextColorChange,
                                isQuickColorPresetChangeEnabled = isQuickColorPresetChangeEnabled,
                                onQuickColorPresetChangeEnabledChange = onQuickColorPresetChangeEnabledChange
                            )
                        }
                    }
                }
            }
        }
    }
}


// 阅读模式、边距、系统、杂项
@Composable
private fun GeneralContent(
    // 阅读模式参数
    pageTurnMode: ReadingMode,
    onPageTurnModeChange: (ReadingMode) -> Unit,
    // 边距参数
    sidePadding: Int,
    onSidePaddingChange: (Int) -> Unit,
    verticalPadding: Int,
    onVerticalPaddingChange: (Int) -> Unit,
    cutoutPaddingApply: Boolean,
    onCutoutPaddingApplyChange: (Boolean) -> Unit,
    bottomMargin: Int,
    onBottomMarginChange: (Int) -> Unit,
    // 系统参数
    customBrightnessEnabled: Boolean,
    onCustomBrightnessEnabledChange: (Boolean) -> Unit,
    customBrightnessValue: Float,
    onCustomBrightnessValueChange: (Float) -> Unit,
    screenOrientation: ScreenOrientation,
    onScreenOrientationChange: (ScreenOrientation) -> Unit,
    // 杂项参数
    isFullScreen: Boolean,
    onIsFullScreenChange: (Boolean) -> Unit,
    isKeepScreenOn: Boolean,
    onIsKeepScreenOnChange: (Boolean) -> Unit,
    isHideBarWhenQuickScroll: Boolean,
    onIsHideBarWhenQuickScrollChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    scrollState: ScrollState = rememberScrollState(),
) {
    Box(modifier = modifier.fillMaxHeight(0.7f)) {
        Column(modifier = Modifier.verticalScroll(scrollState)) {
            // 阅读模式
            SettingsGroupTitle(
                title = {
                    stringResource(id = R.string.reader_settings_reader_mode)
                },
                modifier = Modifier.padding(
                    top = 16.dp,
                    bottom = 8.dp,
                    start = 16.dp,
                    end = 16.dp
                )
            )

            SettingsSubGroupTitle(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                title = stringResource(id = R.string.reader_settings_page_turn_mode)
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                ReadingMode.entries.forEach { pageAnim ->
                    SelectionChip(
                        text = stringResource(pageAnim.labelResId),
                        isSelected = pageAnim == pageTurnMode,
                        onClick = {
                            onPageTurnModeChange(pageAnim)
                        },
                    )
                }
            }

            // 边距
            SettingsGroupTitle(
                title = {
                    stringResource(id = R.string.reader_settings_margin_title)
                },
                modifier = Modifier.padding(
                    top = 16.dp,
                    bottom = 8.dp,
                    start = 16.dp,
                    end = 16.dp
                )
            )

            SettingsItemWithIntSlider(
                modifier = Modifier.padding(horizontal = 16.dp),
                title = stringResource(id = R.string.reader_settings_side_padding_title),
                subtitleResId = R.string.reader_settings_side_padding_format,
                initialValue = sidePadding,
                onSave = onSidePaddingChange,
                valueRange = AppConstants.SIDE_PADDING_INT_RANGE,
            )

            SettingsItemWithIntSlider(
                modifier = Modifier.padding(horizontal = 16.dp),
                title = stringResource(id = R.string.reader_settings_vertical_padding),
                subtitleResId = R.string.reader_settings_vertical_padding_format,
                initialValue = verticalPadding,
                onSave = onVerticalPaddingChange,
                valueRange = AppConstants.VERTICAL_PADDING_INT_RANGE,
            )

            SettingsItemWithSwitch(
                title = stringResource(R.string.reader_settings_cutout_padding_title),
                subtitle = stringResource(R.string.reader_settings_cutout_padding_subtitle),
                checked = cutoutPaddingApply,
                onCheckedChange = onCutoutPaddingApplyChange
            )

            SettingsItemWithIntSlider(
                modifier = Modifier.padding(horizontal = 16.dp),
                title = stringResource(id = R.string.reader_settings_bottom_margin_title),
                subtitleResId = R.string.reader_settings_bottom_margin_format,
                initialValue = bottomMargin,
                onSave = onBottomMarginChange,
                valueRange = AppConstants.BOTTOM_MARGIN_INT_RANGE,
            )

            // 系统
            SettingsGroupTitle(
                title = {
                    stringResource(id = R.string.reader_settings_system)
                },
                modifier = Modifier.padding(
                    top = 16.dp,
                    bottom = 8.dp,
                    start = 16.dp,
                    end = 16.dp
                )
            )

            // 自定义亮度
            SettingsItemWithSwitch(
                title = stringResource(R.string.reader_settings_custom_brightness_title),
                checked = customBrightnessEnabled,
                onCheckedChange = onCustomBrightnessEnabledChange
            )

            // 亮度
            AnimatedVisibility(customBrightnessEnabled) {
                SettingsItemWithFloatSlider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    title = stringResource(id = R.string.reader_settings_brightness_title),
                    subtitleResId = R.string.reader_settings_brightness_format,
                    initialValue = customBrightnessValue,
                    onSave = onCustomBrightnessValueChange,
                    floatRange = AppConstants.CUSTOM_BRIGHTNESS_FLOAT_RANGE
                )
            }

            // 屏幕方向
            SettingsSubGroupTitle(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                title = stringResource(id = R.string.reader_settings_screen_orientation_title)
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                ScreenOrientation.entries.forEach { orientation ->
                    SelectionChip(
                        text = stringResource(orientation.labelResId),
                        isSelected = orientation == screenOrientation,
                        onClick = {
                            onScreenOrientationChange(orientation)
                        },
                    )
                }
            }

            // 杂项
            SettingsGroupTitle(
                title = {
                    stringResource(id = R.string.reader_settings_miscellaneous)
                },
                modifier = Modifier.padding(
                    top = 16.dp,
                    bottom = 8.dp,
                    start = 16.dp,
                    end = 16.dp
                )
            )

            // 全屏
            SettingsItemWithSwitch(
                title = stringResource(R.string.reader_settings_full_screen_title),
                subtitle = stringResource(R.string.reader_settings_full_screen_subtitle),
                checked = isFullScreen,
                onCheckedChange = onIsFullScreenChange
            )

            // 保持屏幕常亮
            SettingsItemWithSwitch(
                title = stringResource(R.string.reader_settings_keep_screen_on_title),
                checked = isKeepScreenOn,
                onCheckedChange = onIsKeepScreenOnChange
            )

            // 快速滚动时隐藏条栏
            SettingsItemWithSwitch(
                title = stringResource(R.string.reader_settings_hide_bar_when_quick_scroll_title),
                checked = isHideBarWhenQuickScroll,
                onCheckedChange = onIsHideBarWhenQuickScrollChange
            )

            // 添加底部间距
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// 字体、文本、图片、章节、进度
@Composable
private fun ReaderContent(
    // 字体相关参数
    fonts: List<AppFontFamily>,
    fontId: String,
    onFontIdChange: (String) -> Unit,
    fontSize: Int,
    onFontSizeChange: (Int) -> Unit,
    fontWeight: AppFontWeight,
    onFontWeightChange: (AppFontWeight) -> Unit,
    isReaderItalic: Boolean,
    onItalicChange: (Boolean) -> Unit,
    letterSpacing: Int,
    onLetterSpacingChange: (Int) -> Unit,

    // 文本相关参数
    appTextAlign: AppTextAlign,
    onTextAlignChange: (AppTextAlign) -> Unit,
    lineHeight: Int,
    onLineHeightChange: (Int) -> Unit,
    paragraphSpacing: Int,
    onParagraphSpacingChange: (Int) -> Unit,
    paragraphIndent: Int,
    onParagraphIndentChange: (Int) -> Unit,

    // 图片相关参数
    imageVisible: Boolean,
    onImageVisibleChange: (Boolean) -> Unit,
    imageCaptionVisible: Boolean,
    onImageCaptionVisibleChange: (Boolean) -> Unit,
    imageColorEffect: ImageColorEffect,
    onImageColorEffectChange: (ImageColorEffect) -> Unit,
    imageCornerRadius: Int,
    onImageCornerRadiusChange: (Int) -> Unit,
    imageAlign: AppImageAlign,
    onImageAlignChange: (AppImageAlign) -> Unit,
    imageSizePercent: Float,
    onImageSizePercentChange: (Float) -> Unit,

    // 章节相关参数
    chapterTitleAlign: AppChapterTitleAlign,
    onChapterTitleAlignChange: (AppChapterTitleAlign) -> Unit,

    // 进度相关参数
    bottomProgressTextVisible: Boolean,
    onProgressBarVisibleChange: (Boolean) -> Unit,
    progressBarFontSize: Int,
    onProgressBarFontSizeChange: (Int) -> Unit,
    progressBarMargin: Int,
    onProgressBarMarginChange: (Int) -> Unit,
    progressBarTextAlign: ProgressTextAlign,
    onProgressBarTextAlignChange: (ProgressTextAlign) -> Unit,

    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxHeight(0.7f)) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            // 字体
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
                        (AppFontFamily.Default.id == fontFamily.id) -> {
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
                AppFontWeight.entries.forEach { weight ->
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



            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))


            // 文本
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


            // 行高和段落间距
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




            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))


            // 图片
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


            SettingsItemWithSwitch(
                title = stringResource(R.string.reader_settings_image_show_title),
                subtitle = stringResource(R.string.reader_settings_image_show_subtitle),
                checked = imageVisible,
                onCheckedChange = onImageVisibleChange
            )


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

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))


            // 章节
            SettingsGroupTitle(
                title = {
                    stringResource(id = R.string.reader_settings_chapter_title)
                },
                modifier = Modifier.padding(
                    top = 16.dp,
                    bottom = 8.dp,
                    start = 16.dp,
                    end = 16.dp
                )
            )

            // 章节标题对齐
            SettingsSubGroupTitle(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                title = stringResource(id = R.string.reader_settings_chapter_title_align)
            )

            LitheSegmentedButton(
                items = AppChapterTitleAlign.entries.map { align ->
                    OptionItem(
                        label = stringResource(align.labelResId),
                        value = align,
                        selected = align == chapterTitleAlign
                    )
                },
                onClick = onChapterTitleAlignChange,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // 进度
            SettingsGroupTitle(
                title = {
                    stringResource(id = R.string.reader_settings_progress_title)
                },
                modifier = Modifier.padding(
                    top = 16.dp,
                    bottom = 8.dp,
                    start = 16.dp,
                    end = 16.dp
                )
            )

            SettingsItemWithSwitch(
                title = stringResource(R.string.reader_settings_progress_bar_title),
                subtitle = stringResource(R.string.reader_settings_progress_bar_subtitle),
                checked = bottomProgressTextVisible,
                onCheckedChange = onProgressBarVisibleChange
            )

            AnimatedVisibility(bottomProgressTextVisible) {
                Column {
                    SettingsItemWithIntSlider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        title = stringResource(id = R.string.reader_settings_progress_text_size_title),
                        subtitleResId = R.string.reader_settings_progress_text_size_format,
                        initialValue = progressBarFontSize,
                        onSave = onProgressBarFontSizeChange,
                        valueRange = AppConstants.PROGRESS_TEXT_SIZE_INT_RANGE
                    )

                    SettingsItemWithIntSlider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        title = stringResource(id = R.string.reader_settings_progress_bar_margin_title),
                        subtitleResId = R.string.reader_settings_progress_bar_margin_format,
                        initialValue = progressBarMargin,
                        onSave = onProgressBarMarginChange,
                        valueRange = AppConstants.PROGRESS_PADDING_INT_RANGE
                    )


                    LitheSegmentedButton(
                        items = ProgressTextAlign.entries.map { align ->
                            OptionItem(
                                label = stringResource(align.labelResId),
                                value = align,
                                selected = align == progressBarTextAlign
                            )
                        },
                        onClick = onProgressBarTextAlignChange,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}


@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
private fun ColorContent(
    modifier: Modifier = Modifier,
    // 颜色预设
    colorPresets: List<ColorPreset>,
    currentColorPreset: ColorPreset,
    onCurrentColorPresetChange: (ColorPreset) -> Unit,
    onColorPresetsChange: (List<ColorPreset>) -> Unit,
    lazyListState: LazyListState = rememberLazyListState(),
    // 修改颜色预设名称
    onColorPresetNameChange: (ColorPreset, String) -> Unit,
    // 删除颜色预设
    onColorPresetDelete: (ColorPreset) -> Unit,
    // 打乱颜色预设值
    onColorPresetShuffle: (ColorPreset) -> Unit,
    // 添加颜色预设
    onColorPresetAdd: () -> Unit,
    // 改变颜色预设-背景颜色
    onColorPresetBgColorChange: (ColorPreset, Int) -> Unit,
    // 改变颜色预设-文本颜色
    onColorPresetTextColorChange: (ColorPreset, Int) -> Unit,
    // 是否启用快速修改颜色预设
    isQuickColorPresetChangeEnabled: Boolean,
    onQuickColorPresetChangeEnabledChange: (Boolean) -> Unit
) {
    // 本地的颜色预设
    val localPresets = remember(colorPresets) {
        colorPresets.toMutableList()
    }

    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        // 更新颜色预设列表顺序
        localPresets.apply {
            add(to.index, removeAt(from.index))
        }
    }

    val scrollState = rememberScrollState()


    Box(modifier = modifier.fillMaxHeight(0.55f)) { // 固定高度，防止抖动
        Column(
            modifier = Modifier.verticalScroll(
                scrollState
            )
        ) {
            SettingsSubGroupTitle(
                title = stringResource(R.string.color_presets),
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                state = lazyListState,
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(items = localPresets, key = { it.id }) { preset ->
                    // 可排序的列表项
                    ReorderableItem(
                        state = reorderableLazyListState,
                        key = preset.id
                    ) { isDragging ->
                        val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp)

                        ColorPresetOption(
                            colorPreset = preset,
                            onClick = {
                                // 更新当前颜色预设
                                onCurrentColorPresetChange(preset)
                            },
                            dragEnabled = preset.isSelected && localPresets.size > 1,
                            elevation = elevation,
                            onDragStopped = {
                                // 更新颜色预设列表
                                onColorPresetsChange(localPresets)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // --- 颜色预设编辑器 ---
            ColorPresetEditorCard(
                colorPreset = currentColorPreset,
                onColorPresetNameChange = onColorPresetNameChange,
                onDelete = onColorPresetDelete,
                onShuffle = onColorPresetShuffle,
                onCreateNew = onColorPresetAdd,
                onBgColorChange = onColorPresetBgColorChange,
                onTextColorChange = onColorPresetTextColorChange,
            )

            Spacer(modifier = Modifier.height(12.dp))

            SettingsItemWithSwitch(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                title = stringResource(R.string.quick_color_preset_change),
                subtitle = stringResource(R.string.swipe_to_change_color_preset),
                checked = isQuickColorPresetChangeEnabled,
                onCheckedChange = onQuickColorPresetChangeEnabledChange
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}