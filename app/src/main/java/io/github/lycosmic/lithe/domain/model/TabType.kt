package io.github.lycosmic.lithe.domain.model

import androidx.annotation.StringRes
import io.github.lycosmic.lithe.R

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