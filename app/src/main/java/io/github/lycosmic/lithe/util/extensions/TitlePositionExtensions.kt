package io.github.lycosmic.lithe.util.extensions

import io.github.lycosmic.lithe.R
import io.github.lycosmic.model.BookTitlePosition

/**
 * 获取书籍标题位置的标签资源 ID
 */
val BookTitlePosition.labelResId
    get() = when (this) {
        BookTitlePosition.Hidden -> R.string.hidden
        BookTitlePosition.Below -> R.string.below
        BookTitlePosition.Inside -> R.string.inside
    }