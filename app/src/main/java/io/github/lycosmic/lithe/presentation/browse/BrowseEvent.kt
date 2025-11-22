package io.github.lycosmic.lithe.presentation.browse

import io.github.lycosmic.lithe.data.model.FileItem

sealed class BrowseEvent {

    /**
     * 点击书籍添加按钮
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
}