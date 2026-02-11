package io.github.lycosmic.lithe.util.extensions

import io.github.lycosmic.data.settings.ProgressRecord
import io.github.lycosmic.lithe.R


val ProgressRecord.labelResId: Int
    get() = when (this) {
        ProgressRecord.Percentage -> R.string.reader_settings_progress_percentage
        ProgressRecord.Count -> R.string.reader_settings_progress_count
    }