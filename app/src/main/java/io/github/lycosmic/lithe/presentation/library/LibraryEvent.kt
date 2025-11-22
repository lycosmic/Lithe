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
     * 点击设置
     */
    data object OnSettingsClicked : LibraryEvent()

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
}