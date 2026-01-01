package io.github.lycosmic.lithe.domain.model

import io.github.lycosmic.lithe.R

/**
 * 书籍标题的位置
 */
enum class BookTitlePosition(val labelResId: Int) {
    Hidden(R.string.hidden),  // 隐藏
    Below(R.string.below),   // 下方
    Inside(R.string.inside)   // 内部
}