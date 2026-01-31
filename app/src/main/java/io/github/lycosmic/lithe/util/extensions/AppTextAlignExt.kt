package io.github.lycosmic.lithe.util.extensions

import io.github.lycosmic.data.settings.AppTextAlign
import io.github.lycosmic.lithe.R


val AppTextAlign.labelResId: Int
    get() = when (this) {
        AppTextAlign.START -> R.string.reader_settings_text_align_start
        AppTextAlign.JUSTIFY -> R.string.reader_settings_text_align_justify
        AppTextAlign.CENTER -> R.string.reader_settings_text_align_center
        AppTextAlign.END -> R.string.reader_settings_text_align_end
    }
