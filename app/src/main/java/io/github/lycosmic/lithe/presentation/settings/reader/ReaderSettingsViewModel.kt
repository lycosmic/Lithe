package io.github.lycosmic.lithe.presentation.settings.reader

import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.lycosmic.data.settings.AppChapterTitleAlign
import io.github.lycosmic.data.settings.AppImageAlign
import io.github.lycosmic.data.settings.AppTextAlign
import io.github.lycosmic.data.settings.ImageColorEffect
import io.github.lycosmic.data.settings.ProgressTextAlign
import io.github.lycosmic.data.settings.ReadingMode
import io.github.lycosmic.data.settings.ScreenOrientation
import io.github.lycosmic.data.settings.SettingsManager
import io.github.lycosmic.domain.model.AppFontFamily
import io.github.lycosmic.domain.model.AppFontWeight
import io.github.lycosmic.domain.repository.FontFamilyRepository
import io.github.lycosmic.lithe.util.AppConstants
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReaderSettingsViewModel @Inject constructor(
    private val settings: SettingsManager,
    private val fontFamilyRepository: FontFamilyRepository
) : ViewModel() {
    // --- 字体 ---
    val fontId = settings.readerFontId.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = AppFontFamily.Default.id
    )

    val fontSize = settings.readerFontSize.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = SettingsManager.DEFAULT_FONT_SIZE
    )

    val fontWeight = settings.appFontWeight.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = AppFontWeight.Normal
    )

    val isItalic = settings.isReaderItalic.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = false
    )

    val letterSpacing = settings.readerLetterSpacing.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = SettingsManager.DEFAULT_LETTER_SPACING
    )

    /**
     * 可使用的字体列表
     */
    val availableFonts = fontFamilyRepository.getAvailableFonts()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
            emptyList()
        )

    val fontFamily = combine(fontId, fontWeight, isItalic) { fontId, fontWeight, isItalic ->
        fontFamilyRepository.getFontFamily(fontId, fontWeight, isItalic) as FontFamily
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        FontFamily.Default
    )

    // --- 文本 ---
    val appTextAlign = settings.readerTextAlign.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = AppTextAlign.JUSTIFY
    )

    val lineHeight = settings.readerLineHeight.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = SettingsManager.DEFAULT_LINE_HEIGHT
    )

    val paragraphSpacing = settings.readerParagraphSpacing.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = SettingsManager.DEFAULT_PARAGRAPH_SPACING
    )

    val paragraphIndent = settings.readerParagraphIndent.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = SettingsManager.DEFAULT_PARAGRAPH_INDENT
    )


    // --- 图片 ---
    val imageVisible = settings.readerImageEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = true
    )

    val imageCaptionVisible = settings.readerImageCaptionEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = true
    )

    val imageColorEffect = settings.readerImageColorEffect.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = ImageColorEffect.NONE
    )

    val imageCornerRadius = settings.readerImageCornerRadius.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = SettingsManager.DEFAULT_IMAGE_CORNER_RADIUS
    )

    val imageAlign = settings.readerImageAlign.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = AppImageAlign.CENTER
    )

    val imageSizeRatio = settings.readerImageSizePercent.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = SettingsManager.DEFAULT_IMAGE_SIZE_PERCENT
    )

    // --- 章节 ---
    val chapterTitleAlign = settings.readerChapterTitleAlign.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = AppChapterTitleAlign.CENTER
    )

    // --- 阅读模式 ---
    val readerMode = settings.readerMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = ReadingMode.SCROLL
    )

    // --- 边距 ---
    val sidePadding = settings.readerSidePadding.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = 1
    )

    val verticalPadding = settings.readerVerticalPadding.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = 0
    )

    val cutoutPaddingApply = settings.readerCutoutPaddingApply.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = true
    )

    val bottomMargin = settings.readerBottomBarPadding.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = 0
    )

    // --- 系统 ---
    val isCustomBrightness = settings.readerCustomBrightnessEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = false
    )

    val customBrightness = settings.readerCustomBrightness.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = 0.5f
    )

    val screenOrientation = settings.screenOrientation.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = ScreenOrientation.DEFAULT
    )

    // --- 进度 ---
    // 底部是否显示进度条
    val isBottomProgressVisible = settings.showProgressText.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = true
    )

    // 进度条文字字体大小
    val progressTextSize = settings.progressBarFontSize.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = 4
    )

    // 进度条文字边距
    val bottomProgressPadding = settings.progressBarMargin.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = 0
    )

    // 进度条文字对齐方式
    val bottomProgressTextAlign = settings.progressBarTextAlign.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = ProgressTextAlign.CENTER
    )


    // --- 杂项 ---
    // 是否全屏
    val isFullScreen = settings.isFullScreenEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = true
    )

    // 保持屏幕常亮
    val screenAlive = settings.keepScreenOn.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = true
    )

    // 快速滚动时隐藏上下栏
    val isHideBarOnQuickScroll = settings.hideBarWhenQuickScroll.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = true
    )


    fun onEvent(event: ReaderSettingsEvent) {
        viewModelScope.launch {
            when (event) {
                is ReaderSettingsEvent.OnFontIdChange -> {
                    settings.setReaderFontId(event.fontId)
                }

                is ReaderSettingsEvent.OnFontSizeChange -> {
                    settings.setReaderFontSize(event.fontSize)
                }

                is ReaderSettingsEvent.OnFontWeightChange -> {
                    settings.setReaderFontWeight(event.fontWeight)
                }

                is ReaderSettingsEvent.OnItalicChange -> {
                    settings.setReaderItalic(event.isItalic)
                }

                is ReaderSettingsEvent.OnLetterSpacingChange -> {
                    settings.setReaderLetterSpacing(event.letterSpacing)
                }

                is ReaderSettingsEvent.OnImageAlignChange -> settings.setReaderImageAlign(event.align)
                is ReaderSettingsEvent.OnImageCaptionVisibleChange -> settings.setReaderImageCaptionEnabled(
                    event.isVisible
                )

                is ReaderSettingsEvent.OnImageColorEffectChange -> settings.setReaderImageColorEffect(
                    event.effect
                )

                is ReaderSettingsEvent.OnImageCornerRadiusChange -> settings.setReaderImageCornerRadius(
                    event.radius
                )

                is ReaderSettingsEvent.OnImageSizePercentChange -> {
                    settings.setReaderImageSizePercent(
                        event.percent
                    )
                }

                is ReaderSettingsEvent.OnImageVisibleChange -> settings.setReaderImageEnabled(event.isVisible)
                is ReaderSettingsEvent.OnLineHeightChange -> settings.setReaderLineHeight(event.lineHeight)
                is ReaderSettingsEvent.OnParagraphIndentChange -> settings.setReaderParagraphIndent(
                    event.paragraphIndent
                )

                is ReaderSettingsEvent.OnParagraphSpacingChange -> settings.setReaderParagraphSpacing(
                    event.paragraphSpacing
                )

                is ReaderSettingsEvent.OnTextAlignChange -> settings.setReaderTextAlign(event.textAlign)
                is ReaderSettingsEvent.OnBottomMarginChange -> settings.setReaderBottomMargin(event.margin)
                is ReaderSettingsEvent.OnChapterTitleAlignChange -> settings.setReaderChapterTitleAlign(
                    event.align
                )

                is ReaderSettingsEvent.OnCustomBrightnessEnabledChange -> settings.setReaderCustomBrightnessEnabled(
                    event.isEnabled
                )

                is ReaderSettingsEvent.OnCustomBrightnessValueChange -> settings.setReaderCustomBrightness(
                    event.value
                )

                is ReaderSettingsEvent.OnCutoutPaddingApplyChange -> settings.setReaderCutoutPaddingApply(
                    event.isApply
                )

                is ReaderSettingsEvent.OnIsFullScreenChange -> settings.setIsFullScreenEnabled(event.isFullScreen)
                is ReaderSettingsEvent.OnIsHideBarWhenQuickScrollChange -> settings.setHideBarWhenQuickScroll(
                    event.isHide
                )

                is ReaderSettingsEvent.OnIsKeepScreenOnChange -> settings.setKeepScreenOn(event.isKeep)
                is ReaderSettingsEvent.OnPageTurnModeChange -> settings.setReaderMode(event.mode)
                is ReaderSettingsEvent.OnProgressBarFontSizeChange -> settings.setProgressBarFontSize(
                    event.size
                )

                is ReaderSettingsEvent.OnProgressBarMarginChange -> settings.setProgressBarMargin(
                    event.margin
                )

                is ReaderSettingsEvent.OnProgressBarTextAlignChange -> settings.setProgressBarTextAlign(
                    event.align
                )

                is ReaderSettingsEvent.OnProgressBarVisibleChange -> settings.setShowProgressBar(
                    event.isVisible
                )

                is ReaderSettingsEvent.OnScreenOrientationChange -> settings.setScreenOrientation(
                    event.orientation
                )

                is ReaderSettingsEvent.OnSidePaddingChange -> settings.setReaderSidePadding(event.padding)
                is ReaderSettingsEvent.OnVerticalPaddingChange -> settings.setReaderVerticalPadding(
                    event.padding
                )
            }
        }
    }
}