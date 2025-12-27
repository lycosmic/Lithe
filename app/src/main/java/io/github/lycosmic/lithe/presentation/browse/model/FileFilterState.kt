package io.github.lycosmic.lithe.presentation.browse.model

import io.github.lycosmic.lithe.domain.model.FilterOption
import io.github.lycosmic.lithe.domain.model.SortType

/**
 * 文件过滤状态
 */
data class FileFilterState(
    val sortType: SortType,
    val isAscending: Boolean,
    val filterOptions: List<FilterOption>,
    val searchText: String,
    // 数据库中的书籍文件Uri列表
    val bookFileUris: Set<String>
)