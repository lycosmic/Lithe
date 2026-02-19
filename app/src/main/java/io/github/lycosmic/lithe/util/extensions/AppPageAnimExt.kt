package io.github.lycosmic.lithe.util.extensions

import io.github.lycosmic.data.settings.ReadingMode
import io.github.lycosmic.lithe.R


val ReadingMode.labelResId: Int
    get() = when (this) {
        ReadingMode.SLIDE -> R.string.reader_settings_page_anim_slide
        ReadingMode.SCROLL -> R.string.reader_settings_page_anim_scroll
        ReadingMode.NONE -> R.string.reader_settings_page_anim_none
    }