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

    /**
     * 改变应用主题
     */
    data class OnAppThemeChange(val themeId: String) : AppearanceSettingsEvent()

    /**
     * 导航栏标签可见性改变
     */
    data class OnNavLabelVisibleChange(val visible: Boolean) : AppearanceSettingsEvent()
}