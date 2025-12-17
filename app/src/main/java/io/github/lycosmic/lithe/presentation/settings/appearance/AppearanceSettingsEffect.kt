package io.github.lycosmic.lithe.presentation.settings.appearance

sealed class AppearanceSettingsEffect {
    /**
     * 导航至上一页
     */
    object NavigateBack : AppearanceSettingsEffect()
}