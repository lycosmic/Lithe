package io.github.lycosmic.lithe.presentation.settings.library

sealed class LibrarySettingsEvent {
    /**
     * 点击了返回按钮
     */
    object OnBackClick : LibrarySettingsEvent()

    /**
     * 点击了创建分类
     */
    object OnCreateCategoryClick : LibrarySettingsEvent()
}