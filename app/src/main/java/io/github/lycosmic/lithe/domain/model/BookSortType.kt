package io.github.lycosmic.lithe.domain.model

import androidx.annotation.StringRes
import io.github.lycosmic.lithe.R

enum class BookSortType(
    @param:StringRes
    val displayNameResId: Int
) {
    // 字母顺序
    ALPHABETICAL(R.string.book_sort_type_alphabetical),

    // 上次阅读时间
    LAST_READ_TIME(R.string.book_sort_type_last_read_time),

    // 进度
    PROGRESS(R.string.book_sort_type_progress),

    // 作者
    AUTHOR(R.string.book_sort_type_author);

    companion object {
        // 默认按上次阅读时间排序
        val DEFAULT_BOOK_SORT_TYPE = LAST_READ_TIME
    }
}