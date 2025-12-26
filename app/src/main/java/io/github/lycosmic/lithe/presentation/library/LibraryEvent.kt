package io.github.lycosmic.lithe.presentation.library

import io.github.lycosmic.lithe.data.model.BookSortType
import io.github.lycosmic.lithe.data.model.BookTitlePosition
import io.github.lycosmic.lithe.data.model.DisplayMode


sealed class LibraryEvent {
    // --- 书籍区域 ---
    /**
     * 点击书籍
     */
    data class OnBookClicked(val bookId: Long) : LibraryEvent()

    /**
     * 长按书籍
     */
    data class OnBookLongClicked(val bookId: Long) : LibraryEvent()

    /**
     * 点击开始阅读
     */
    data class OnStartReadingClicked(val bookId: Long) : LibraryEvent()

    /**
     * 点击添加书籍
     */
    data object OnAddBookClicked : LibraryEvent()

    // --- 通用顶部栏区域 ---
    /**
     * 点击更多
     */
    data object OnMoreClicked : LibraryEvent()

    /**
     * 点击关于
     */
    data object OnAboutClicked : LibraryEvent()

    /**
     * 点击帮助
     */
    data object OnHelpClicked : LibraryEvent()

    /**
     * 点击设置
     */
    object OnSettingsClicked : LibraryEvent()


    // --- 默认顶部栏区域---
    /**
     * 点击搜索图标
     */
    object OnSearchClicked : LibraryEvent()

    /**
     * 点击过滤按钮
     */
    object OnFilterClicked : LibraryEvent()


    /**
     * 点击过滤底部抽屉取消
     */
    object OnFilterBottomSheetDismissed : LibraryEvent()

    // --- 过滤底部抽屉 ---
    /**
     * 排序方式改变
     */
    data class OnSortChanged(val sortType: BookSortType, val isAscending: Boolean) : LibraryEvent()

    /**
     * 显示模式改变
     */
    data class OnDisplayModeChanged(val displayMode: DisplayMode) : LibraryEvent()

    /**
     * 网格大小改变
     */
    data class OnGridSizeChanged(val gridSize: Int) : LibraryEvent()

    /**
     * 书籍标题位置改变
     */
    data class OnBookTitlePositionChanged(val titlePosition: BookTitlePosition) : LibraryEvent()

    /**
     * 阅读按钮可见性改变
     */
    data class OnReadButtonVisibleChanged(val visible: Boolean) : LibraryEvent()

    /**
     * 阅读进度可见性改变
     */
    data class OnProgressVisibleChanged(val visible: Boolean) : LibraryEvent()

    /**
     * 分类标签可见性改变
     */
    data class OnCategoryTabVisibleChanged(val visible: Boolean) : LibraryEvent()

    /**
     * 默认分类标签可见性改变
     */
    data class OnDefaultCategoryTabVisibleChanged(val visible: Boolean) : LibraryEvent()

    /**
     * 书籍数量可见性改变
     */
    data class OnBookCountVisibleChanged(val visible: Boolean) : LibraryEvent()

    // --- 选择模式顶部栏区域 ---
    /**
     * 点击取消所有选中的书籍
     */
    data object OnCancelSelectionClicked : LibraryEvent()

    /**
     * 点击全选书籍
     */
    data object OnSelectAllClicked : LibraryEvent()


    /**
     * 点击移动书籍分类
     */
    object OnMoveCategoryClicked : LibraryEvent()

    /**
     * 点击删除书籍
     */
    data object OnDeleteClicked : LibraryEvent()

    /**
     * 点击删除书籍对话框取消
     */
    object OnDeleteDialogDismissed : LibraryEvent()

    /**
     * 点击删除书籍对话框确认
     */
    object OnDeleteDialogConfirmed : LibraryEvent()


    // --- 搜索模式顶部栏区域 ---
    /**
     * 点击退出搜索
     */
    object OnExitSearchClicked : LibraryEvent()

    /**
     * 搜索文本改变
     */
    data class OnSearchTextChanged(val text: String) : LibraryEvent()
}