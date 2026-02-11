package io.github.lycosmic.lithe.util.extensions

import io.github.lycosmic.data.settings.ScreenOrientation
import io.github.lycosmic.lithe.R


val ScreenOrientation.labelResId: Int
    get() = when (this) {
        ScreenOrientation.DEFAULT -> R.string.reader_settings_screen_orientation_default
        ScreenOrientation.FREE -> R.string.reader_settings_screen_orientation_free
        ScreenOrientation.PORTRAIT -> R.string.reader_settings_screen_orientation_portrait
        ScreenOrientation.LANDSCAPE -> R.string.reader_settings_screen_orientation_landscape
        ScreenOrientation.PORTRAIT_LOCKED -> R.string.reader_settings_screen_orientation_portrait_locked
        ScreenOrientation.LANDSCAPE_LOCKED -> R.string.reader_settings_screen_orientation_landscape_locked
    }