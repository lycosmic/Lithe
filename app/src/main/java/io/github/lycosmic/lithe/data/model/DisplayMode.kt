package io.github.lycosmic.lithe.data.model

import io.github.lycosmic.lithe.R

/**
 * 浏览文件夹的显示模式
 */
enum class DisplayMode(val labelResId: Int) {
    List(R.string.list),
    Grid(R.string.grid)
}