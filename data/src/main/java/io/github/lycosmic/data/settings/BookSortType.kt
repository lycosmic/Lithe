package io.github.lycosmic.data.settings

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
        fun fromName(name: String?, default: BookSortType = LAST_READ_TIME): BookSortType {
            return entries.find { it.name == name } ?: default
        }
    }
}