package io.github.lycosmic.lithe.util.extensions

import io.github.lycosmic.domain.model.BookSortType
import io.github.lycosmic.domain.model.FileSortType
import io.github.lycosmic.lithe.R


/**
 * 书籍排序类型的标签资源 ID
 */
val BookSortType.labelResId: Int
    get() = when (this) {
        BookSortType.ALPHABETICAL -> R.string.book_sort_type_alphabetical
        BookSortType.LAST_READ_TIME -> R.string.book_sort_type_last_read_time
        BookSortType.PROGRESS -> R.string.book_sort_type_progress
        BookSortType.AUTHOR -> R.string.book_sort_type_author
    }


val FileSortType.labelResId: Int
    get() = when (this) {
        FileSortType.ALPHABETICAL -> R.string.sort_type_alphabetical
        FileSortType.FILE_FORMAT -> R.string.sort_type_file_format
        FileSortType.LAST_MODIFIED -> R.string.sort_type_last_modified
        FileSortType.SIZE -> R.string.sort_type_size
    }