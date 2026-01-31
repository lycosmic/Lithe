package io.github.lycosmic.lithe.util.extensions

import io.github.lycosmic.data.settings.AppImageAlign
import io.github.lycosmic.lithe.R


val AppImageAlign.labelResId: Int
    get() = when (this) {
        AppImageAlign.START -> R.string.reader_settings_image_align_start
        AppImageAlign.CENTER -> R.string.reader_settings_image_align_center
        AppImageAlign.END -> R.string.reader_settings_image_align_end
    }