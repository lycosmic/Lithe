package io.github.lycosmic.lithe.util.extensions

import io.github.lycosmic.data.settings.ImageColorEffect
import io.github.lycosmic.lithe.R


val ImageColorEffect.labelResId: Int
    get() = when (this) {
        ImageColorEffect.NONE -> R.string.image_color_effect_none
        ImageColorEffect.GRAYSCALE -> R.string.image_color_effect_grayscale
        ImageColorEffect.FONT_COLOR -> R.string.image_color_effect_font_color
        ImageColorEffect.BACKGROUND_COLOR -> R.string.image_color_effect_background_color
    }