package io.github.lycosmic.lithe.presentation.settings.general


sealed class GeneralSettingsEffect {

    /**
     * 导航至上一页
     */
    object NavigateBack : GeneralSettingsEffect()
}