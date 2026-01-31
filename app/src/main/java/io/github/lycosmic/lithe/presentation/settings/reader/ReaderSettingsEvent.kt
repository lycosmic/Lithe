package io.github.lycosmic.lithe.presentation.settings.reader


sealed class ReaderSettingsEvent {
    data class OnFontIdChange(val fontId: String) : ReaderSettingsEvent()
    data class OnFontSizeChange(val fontSize: Float) : ReaderSettingsEvent()
    data class OnFontWeightChange(val fontWeight: Int) : ReaderSettingsEvent()
    data class OnItalicChange(val isItalic: Boolean) : ReaderSettingsEvent()
    data class OnLetterSpacingChange(val letterSpacing: Float) : ReaderSettingsEvent()
}