package io.github.lycosmic.lithe.domain.model

import io.github.lycosmic.lithe.R

/**
 * 显示模式 - 用于书库中书籍和浏览器文件夹的显示模式
 */
enum class DisplayMode(val labelResId: Int) {
    List(R.string.list),
    Grid(R.string.grid)
}