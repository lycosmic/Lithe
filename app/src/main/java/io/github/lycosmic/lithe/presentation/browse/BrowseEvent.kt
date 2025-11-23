package io.github.lycosmic.lithe.presentation.browse

import io.github.lycosmic.lithe.data.model.FileItem
import io.github.lycosmic.lithe.presentation.browse.model.BookToAdd

sealed class BrowseEvent {

    /**
     * 点击添加书籍按钮
     */
    data object OnAddBooksClick : BrowseEvent()

    /**
     * 点击确定添加书籍按钮 (确定导入)
     */
    data object OnImportBooksClick : BrowseEvent()

    /**
     * 点击全选按钮
     */
    data object OnToggleAllSelectionClick : BrowseEvent()

    /**
     * 点击取消按钮
     */
    data object OnCancelClick : BrowseEvent()

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
     * 在书籍对话框中点击某一项
     */
    data class OnAddBooksDialogItemClick(val item: BookToAdd) : BrowseEvent()
}