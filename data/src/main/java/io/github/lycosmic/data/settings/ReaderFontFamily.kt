package io.github.lycosmic.data.settings

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import io.github.lycosmic.data.R

/**
 * 字体系列
 */
enum class ReaderFontFamily(val id: String, val displayName: String, val family: FontFamily) {
    Default("default", "", FontFamily.Default),
    Garamond("garamond", "Garamond", FontFamily(Font(R.font.garamond))),
    Lora("lora", "Lora", FontFamily(Font(R.font.lora))),
    Merriweather("merriweather", "Merriweather", FontFamily(Font(R.font.merriweather))),
    Montserrat("montserrat", "Montserrat", FontFamily(Font(R.font.montserrat))),
    NotoSans("noto_sans", "Noto Sans", FontFamily(Font(R.font.noto_sans))),
    OpenDyslexic("open_dyslexic", "Open Dyslexic", FontFamily(Font(R.font.open_dyslexic))),
    OpenSans("open_sans", "Open Sans", FontFamily(Font(R.font.open_sans))),
    Raleway("raleway", "Raleway", FontFamily(Font(R.font.raleway))),
    Roboto("roboto", "Roboto", FontFamily(Font(R.font.roboto))),
    RobotoSlab("roboto_slab", "Roboto Slab", FontFamily(Font(R.font.roboto_slab)));

    companion object {
        /**
         * 通过 id 获取字体系列
         */
        fun fromId(id: String) {
            entries.find {
                it.id == id
            } ?: Default
        }
    }
}