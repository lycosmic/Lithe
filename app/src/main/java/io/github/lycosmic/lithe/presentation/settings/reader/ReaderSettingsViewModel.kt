package io.github.lycosmic.lithe.presentation.settings.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.lycosmic.data.settings.ReaderFontFamily
import io.github.lycosmic.data.settings.ReaderFontWeight
import io.github.lycosmic.data.settings.SettingsManager
import io.github.lycosmic.lithe.util.AppConstants
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReaderSettingsViewModel @Inject constructor(
    private val settings: SettingsManager
) : ViewModel() {

    val fontId = settings.readerFontId.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = ReaderFontFamily.Default.id
    )

    val fontSize = settings.readerFontSize.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = SettingsManager.DEFAULT_FONT_SIZE
    )

    val fontWeight = settings.readerFontWeight.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = ReaderFontWeight.Normal.value
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
            }
        }
    }
}