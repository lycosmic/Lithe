package io.github.lycosmic.lithe.presentation.settings.library

import io.github.lycosmic.lithe.data.model.BookTitlePosition
import io.github.lycosmic.lithe.data.model.LibraryDisplayMode

sealed class LibrarySettingsEvent {
    /**
     * 点击了返回按钮
     */
    object OnBackClick : LibrarySettingsEvent()

    /**
     * 点击了创建分类
     */
    object OnCreateCategoryClick : LibrarySettingsEvent()

    /**
     * 书籍显示模式改变
     */
    data class OnBookDisplayModeChange(val libraryDisplayMode: LibraryDisplayMode) :
        LibrarySettingsEvent()

    /**
     * 书籍网格列数改变
     */
    data class OnBookGridColumnCountChange(val columnCount: Int) : LibrarySettingsEvent()

    /**
     * 书籍标题位置改变
     */
    data class OnBookTitlePositionChange(val bookTitlePosition: BookTitlePosition) :
        LibrarySettingsEvent()

    /**
     * 书籍显示阅读按钮改变
     */
    data class OnBookShowReadButtonChange(val showReadButton: Boolean) : LibrarySettingsEvent()

    /**
     * 书籍显示进度改变
     */
    data class OnBookShowProgressChange(val showProgress: Boolean) : LibrarySettingsEvent()

    /**
     * 分类标签页可见性改变
     */
    data class OnCategoryTabVisibleChange(val visible: Boolean) : LibrarySettingsEvent()

    /**
     * 默认分类标签页可见性改变
     */
    data class OnDefaultCategoryTabVisibleChange(val visible: Boolean) : LibrarySettingsEvent()

    /**
     * 分类书籍数可见性改变
     */
    data class OnCategoryBookCountVisibleChange(val visible: Boolean) : LibrarySettingsEvent()


    /**
     * 每个分类不同排序依据改变
     */
    data class OnCategorySortDifferentChange(val sortDifferent: Boolean) : LibrarySettingsEvent()

    /**
     * 创建分类
     */
    data class OnCreateCategory(val name: String) : LibrarySettingsEvent()
}