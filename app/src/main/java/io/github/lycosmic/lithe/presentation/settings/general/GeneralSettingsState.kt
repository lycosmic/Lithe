package io.github.lycosmic.lithe.presentation.settings.general

import androidx.compose.runtime.Immutable


@Immutable
data class GeneralSettingsState(
    val languageCode: String,
    val isDoubleBackToExitEnabled: Boolean
) {
    companion object {
        val DEFAULT = GeneralSettingsState(
            languageCode = AppLanguage.DEFAULT_LANGUAGE_CODE,
            isDoubleBackToExitEnabled = false
        )
    }
}