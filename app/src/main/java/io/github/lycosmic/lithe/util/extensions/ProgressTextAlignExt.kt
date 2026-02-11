package io.github.lycosmic.lithe.util.extensions

import io.github.lycosmic.data.settings.ProgressTextAlign
import io.github.lycosmic.lithe.R


val ProgressTextAlign.labelResId: Int
    get() = when (this) {
        ProgressTextAlign.START -> R.string.reader_settings_progress_text_align_start
        ProgressTextAlign.CENTER -> R.string.reader_settings_progress_text_align_center
        ProgressTextAlign.END -> R.string.reader_settings_progress_text_align_end
    }