package io.github.lycosmic.lithe.presentation.settings.reader

import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.lycosmic.data.settings.AppImageAlign
import io.github.lycosmic.data.settings.AppTextAlign
import io.github.lycosmic.data.settings.ImageColorEffect
import io.github.lycosmic.data.settings.ReaderFontWeight
import io.github.lycosmic.data.settings.SettingsManager
import io.github.lycosmic.domain.model.MyFontFamily
import io.github.lycosmic.domain.repository.FontFamilyRepository
import io.github.lycosmic.lithe.util.AppConstants
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
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
        initialValue = MyFontFamily.Default.id
    )

    val fontSize = settings.readerFontSize.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = SettingsManager.DEFAULT_FONT_SIZE
    )

    val fontWeight = settings.readerFontWeight.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = ReaderFontWeight.Normal
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

    val fontFamily = fontId.map {
        fontFamilyRepository.getFontFamily(it) as FontFamily
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
            }
        }
    }
}