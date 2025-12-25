package io.github.lycosmic.lithe.presentation.browse

import io.github.lycosmic.lithe.data.model.BrowseDisplayMode
import io.github.lycosmic.lithe.data.model.FileItem
import io.github.lycosmic.lithe.domain.model.FilterOption
import io.github.lycosmic.lithe.domain.model.SortType
import io.github.lycosmic.lithe.presentation.browse.model.ParsedBook

sealed class BrowseEvent {

    /**
     * 点击添加文件按钮
     */
    data object OnAddFileClick : BrowseEvent()

    /**
     * 点击导入书籍按钮
     */
    data object OnImportBooksClick : BrowseEvent()

    /**
     * 点击全选按钮
     */
    data object OnToggleAllSelectionClick : BrowseEvent()

    /**
     * 点击清空选中项
     */
    data object OnClearSelectionClick : BrowseEvent()

    /**
     * 用户返回
     */
    data object OnBackClick : BrowseEvent()

    /**
     * 点击文件的复选框
     */
    data class OnFileCheckboxClick(val file: FileItem) : BrowseEvent()

    /**
     * 点击文件
     */
    data class OnFileClick(val file: FileItem) : BrowseEvent()

    /**
     * 长按文件
     */
    data class OnFileLongClick(val files: List<FileItem>) : BrowseEvent()

    /**
     * 离开或是取消添加书籍对话框
     */
    data object OnDismissAddBooksDialog : BrowseEvent()

    /**
     * 点击添加书籍对话框的书籍项
     */
    data class OnBookItemClick(val bookItem: ParsedBook) : BrowseEvent()

    /**
     * 过滤器中的排序类型改变
     */
    data class OnSortTypeChange(val sortType: SortType, val isAscending: Boolean) : BrowseEvent()

    /**
     * 点击过滤器中的文件类型
     */
    data class OnFilterChange(val filterOption: FilterOption) : BrowseEvent()

    /**
     * 点击搜索按钮
     */
    data object OnSearchClick : BrowseEvent()

    /**
     * 点击退出搜索模式
     */
    data object OnExitSearchClick : BrowseEvent()

    /**
     * 搜索框中的文字改变
     */
    data class OnSearchTextChange(val text: String) : BrowseEvent()

    /**
     * 显示模式改变
     */
    data class OnDisplayModeChange(val newDisplayMode: BrowseDisplayMode) : BrowseEvent()

    /**
     * 网格大小改变
     */
    data class OnGridSizeChange(val newGridSize: Int) : BrowseEvent()

    /**
     * 点击固定头部按钮
     */
    data class OnPinHeaderClick(val path: String) : BrowseEvent()
}