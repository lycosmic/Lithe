package io.github.lycosmic.lithe.presentation.settings.browse


sealed class BrowseSettingEffect {
    /**
     * 打开文件夹选择器
     */
    object OpenDirectoryPicker : BrowseSettingEffect()


}