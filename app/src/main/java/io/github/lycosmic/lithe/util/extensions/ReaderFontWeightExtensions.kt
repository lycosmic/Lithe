package io.github.lycosmic.lithe.util.extensions

import io.github.lycosmic.data.settings.ReaderFontWeight
import io.github.lycosmic.lithe.R


/**
 * 字体粗细的显示名称资源 ID
 */
val ReaderFontWeight.displayNameResId: Int
    get() = when (this) {
        ReaderFontWeight.Thin -> R.string.font_weight_thin
        ReaderFontWeight.Light -> R.string.font_weight_light
        ReaderFontWeight.Normal -> R.string.font_weight_normal
        ReaderFontWeight.Medium -> R.string.font_weight_medium
        ReaderFontWeight.Bold -> R.string.font_weight_bold
    }