package io.github.lycosmic.lithe.util.extensions

import io.github.lycosmic.domain.model.AppFontWeight
import io.github.lycosmic.lithe.R


/**
 * 字体粗细的显示名称资源 ID
 */
val AppFontWeight.displayNameResId: Int
    get() = when (this) {
        AppFontWeight.Thin -> R.string.font_weight_thin
        AppFontWeight.Light -> R.string.font_weight_light
        AppFontWeight.Normal -> R.string.font_weight_normal
        AppFontWeight.Medium -> R.string.font_weight_medium
        AppFontWeight.Bold -> R.string.font_weight_bold
    }