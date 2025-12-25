package io.github.lycosmic.lithe.data.model

import io.github.lycosmic.lithe.R

/**
 * 书库中书籍的显示模式
 */
enum class LibraryDisplayMode(val labelResId: Int) {

    /**
     * 网格
     */
    Grid(R.string.library_display_mode_grid),

    /**
     * 列表
     */
    List(R.string.library_display_mode_list),
}