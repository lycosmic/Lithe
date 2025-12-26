package io.github.lycosmic.lithe.presentation.library


sealed class LibraryEvent {
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
     * 点击添加书籍
     */
    data object OnAddBookClicked : LibraryEvent()

    /**
     * 点击取消所有选中的书籍
     */
    data object OnCancelSelectionClicked : LibraryEvent()

    /**
     * 点击全选书籍
     */
    data object OnSelectAllClicked : LibraryEvent()

    /**
     * 点击删除书籍
     */
    data object OnDeleteClicked : LibraryEvent()

    /**
     * 搜索文本改变
     */
    data class OnSearchTextChanged(val text: String) : LibraryEvent()

    /**
     * 点击搜索框
     */
    object OnSearchClicked : LibraryEvent()

    /**
     * 点击退出搜索
     */
    object OnExitSearchClicked : LibraryEvent()

    /**
     * 点击过滤按钮
     */
    object OnFilterClicked : LibraryEvent()

    /**
     * 点击移动书籍分类
     */
    object OnMoveCategoryClicked : LibraryEvent()

    /**
     * 点击设置
     */
    object OnSettingsClicked : LibraryEvent()

    /**
     * 点击删除书籍对话框取消
     */
    object OnDeleteDialogDismissed : LibraryEvent()

    /**
     * 点击删除书籍对话框确认
     */
    object OnDeleteDialogConfirmed : LibraryEvent()
}