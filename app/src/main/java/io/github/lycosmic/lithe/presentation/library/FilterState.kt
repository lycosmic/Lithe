package io.github.lycosmic.lithe.presentation.library

import io.github.lycosmic.lithe.data.model.BookSortType

/**
 * 书籍过滤后的状态
 */
data class BookFilterState(
    val sortType: BookSortType,
    val isAscending: Boolean,
    val isSearching: Boolean,
    val searchText: String
)