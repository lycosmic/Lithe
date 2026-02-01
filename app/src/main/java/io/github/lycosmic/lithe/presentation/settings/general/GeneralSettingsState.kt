package io.github.lycosmic.lithe.presentation.settings.general

import androidx.compose.runtime.Immutable
import io.github.lycosmic.data.settings.AppLanguage


@Immutable
data class GeneralSettingsState(
    val appLanguage: AppLanguage,
    val isDoubleBackToExitEnabled: Boolean
) {
    companion object {
        val DEFAULT = GeneralSettingsState(
            appLanguage = AppLanguage.CHINESE,
            isDoubleBackToExitEnabled = false
        )
    }
}