package io.github.lycosmic.lithe.util.extensions

import io.github.lycosmic.data.settings.AppPageAnim
import io.github.lycosmic.lithe.R


val AppPageAnim.labelResId: Int
    get() = when (this) {
        AppPageAnim.SIMULATION -> R.string.reader_settings_page_anim_simulation
        AppPageAnim.SLIDE -> R.string.reader_settings_page_anim_slide
        AppPageAnim.SCROLL -> R.string.reader_settings_page_anim_scroll
        AppPageAnim.NONE -> R.string.reader_settings_page_anim_none
    }