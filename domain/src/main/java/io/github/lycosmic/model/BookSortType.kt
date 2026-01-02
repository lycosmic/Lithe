package io.github.lycosmic.model


enum class BookSortType {
    // 字母顺序
    ALPHABETICAL,

    // 上次阅读时间
    LAST_READ_TIME,

    // 进度
    PROGRESS,

    // 作者
    AUTHOR;

    companion object {
        // 默认按上次阅读时间排序
        val DEFAULT_BOOK_SORT_TYPE = LAST_READ_TIME
    }
}