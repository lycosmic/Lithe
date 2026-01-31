package io.github.lycosmic.data.settings

import androidx.compose.ui.text.font.FontWeight

/**
 * 字体粗细
 */
enum class ReaderFontWeight(val value: Int, val weight: FontWeight) {
    Thin(100, FontWeight.Thin),
    Light(300, FontWeight.Light),
    Normal(400, FontWeight.Normal),
    Medium(500, FontWeight.Medium),
    Bold(700, FontWeight.Bold);

    companion object {
        fun fromValue(value: Int) = entries.find { it.value == value } ?: Normal
    }
}