package io.github.lycosmic.lithe.presentation.settings.general

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
     * @param languageCode 语言代码
     */
    data class OnLanguageClick(val languageCode: String) : GeneralSettingsEvent()

    /**
     * 点击双击返回退出按钮
     */
    data class OnDoubleClickExitClick(val isChecked: Boolean) : GeneralSettingsEvent()
}