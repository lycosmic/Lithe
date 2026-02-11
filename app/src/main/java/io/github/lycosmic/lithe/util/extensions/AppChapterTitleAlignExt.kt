package io.github.lycosmic.lithe.util.extensions

import io.github.lycosmic.data.settings.AppChapterTitleAlign
import io.github.lycosmic.lithe.R


val AppChapterTitleAlign.labelResId: Int
    get() = when (this) {
        AppChapterTitleAlign.START -> R.string.reader_settings_chapter_title_align_start
        AppChapterTitleAlign.JUSTIFY -> R.string.reader_settings_chapter_title_align_justify
        AppChapterTitleAlign.CENTER -> R.string.reader_settings_chapter_title_align_center
        AppChapterTitleAlign.END -> R.string.reader_settings_chapter_title_align_end
    }