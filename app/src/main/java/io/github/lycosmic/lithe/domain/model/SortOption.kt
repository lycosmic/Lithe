package io.github.lycosmic.lithe.domain.model

import androidx.annotation.StringRes
import io.github.lycosmic.lithe.R

/**
 * 排序类型
 */
enum class SortType(
    @param:StringRes
    val displayNameResId: Int
) {
    // 字母顺序
    ALPHABETICAL(R.string.sort_type_alphabetical),

    // 文件格式
    FILE_FORMAT(R.string.sort_type_file_format),

    // 最后修改
    LAST_MODIFIED(R.string.sort_type_last_modified),

    // 文件大小
    SIZE(R.string.sort_type_size),
}

// 默认排序类型为最后修改
val DEFAULT_SORT_TYPE = SortType.LAST_MODIFIED

// 默认排序顺序为降序
const val DEFAULT_IS_ASCENDING = false

/**
 * 标签页类型
 */
enum class TabType(
    @param:StringRes
    val titleResId: Int
) {
    // 排序
    SORT(R.string.tab_type_sort),

    // 过滤
    FILTER(R.string.tab_type_filter),

    // 显示
    DISPLAY(R.string.tab_type_display),
}