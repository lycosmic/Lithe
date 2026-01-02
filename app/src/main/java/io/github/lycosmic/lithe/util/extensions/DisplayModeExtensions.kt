package io.github.lycosmic.lithe.util.extensions

import io.github.lycosmic.lithe.R
import io.github.lycosmic.model.DisplayMode

/**
 * 显示模式对应的标签资源 ID
 */
val DisplayMode.labelResId: Int
    get() = when (this) {
        DisplayMode.List -> R.string.list
        DisplayMode.Grid -> R.string.grid
    }