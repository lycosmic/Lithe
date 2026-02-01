package io.github.lycosmic.lithe.presentation.settings.general

import io.github.lycosmic.data.settings.AppLanguage

/**
 * 常规设置页面的事件
 */
sealed class GeneralSettingsEvent {
    /**
     * 点击返回按钮
     */
    object NavigateBack : GeneralSettingsEvent()

    /**
     * 点击语言标签
     * @param appLanguage 语言
     */
    data class OnLanguageClick(val appLanguage: AppLanguage) : GeneralSettingsEvent()

    /**
     * 点击双击返回退出按钮
     */
    data class OnDoubleClickExitClick(val isChecked: Boolean) : GeneralSettingsEvent()
}