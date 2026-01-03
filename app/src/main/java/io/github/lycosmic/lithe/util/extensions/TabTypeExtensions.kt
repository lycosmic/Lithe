package io.github.lycosmic.lithe.util.extensions

import io.github.lycosmic.domain.model.TabType
import io.github.lycosmic.lithe.R


/**
 * 获取标签页标题的资源ID
 */
val TabType.titleResId: Int
    get() = when (this) {
        TabType.SORT -> R.string.tab_type_sort
        TabType.FILTER -> R.string.tab_type_filter
        TabType.DISPLAY -> R.string.tab_type_display
    }