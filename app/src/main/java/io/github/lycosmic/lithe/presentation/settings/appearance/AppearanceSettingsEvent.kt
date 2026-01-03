package io.github.lycosmic.lithe.presentation.settings.appearance

import io.github.lycosmic.domain.model.ColorPreset
import io.github.lycosmic.domain.model.ThemeMode


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

    /**
     * 点击了颜色预设
     */
    data class OnColorPresetClick(val preset: ColorPreset) : AppearanceSettingsEvent()

    /**
     * 颜色预设拖拽结束
     */
    data class OnColorPresetDragStopped(val presets: List<ColorPreset>) : AppearanceSettingsEvent()

    /**
     * 点击了删除颜色预设
     */
    data class OnDeleteColorPresetClick(val preset: ColorPreset) : AppearanceSettingsEvent()

    /**
     * 点击了添加颜色预设
     */
    object OnAddColorPresetClick : AppearanceSettingsEvent()

    /**
     * 点击了打乱颜色预设
     */
    data class OnShuffleColorPresetClick(val preset: ColorPreset) : AppearanceSettingsEvent()

    /**
     * 修改颜色预设名称
     */
    data class OnColorPresetNameChange(val preset: ColorPreset, val name: String) :
        AppearanceSettingsEvent()

    /**
     * 颜色预设中背景颜色改变
     */
    data class OnColorPresetBgColorChange(val preset: ColorPreset, val colorArgb: Int) :
        AppearanceSettingsEvent()

    /**
     * 颜色预设中文字颜色改变
     */
    data class OnColorPresetTextColorChange(val preset: ColorPreset, val colorArgb: Int) :
        AppearanceSettingsEvent()

    /**
     * 快速更改颜色预设
     */
    data class OnQuickChangeColorPresetEnabledChange(val enabled: Boolean) :
        AppearanceSettingsEvent()
}