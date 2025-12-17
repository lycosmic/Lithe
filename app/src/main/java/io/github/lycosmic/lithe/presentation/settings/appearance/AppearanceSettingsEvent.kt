package io.github.lycosmic.lithe.presentation.settings.appearance

import io.github.lycosmic.lithe.data.model.ThemeMode

sealed class AppearanceSettingsEvent {
    /**
     * 点击返回按钮
     */
    object OnBackClick : AppearanceSettingsEvent()

    /**
     * 点击主题模式
     */
    data class OnThemeModeClick(val mode: ThemeMode) : AppearanceSettingsEvent()
}