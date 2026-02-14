package io.github.lycosmic.data.util.extensions

import androidx.compose.ui.text.font.FontWeight
import io.github.lycosmic.domain.model.AppFontWeight


val AppFontWeight.weight: FontWeight
    get() = when (this) {
        AppFontWeight.Thin -> FontWeight.Thin
        AppFontWeight.Light -> FontWeight.Light
        AppFontWeight.Normal -> FontWeight.Normal
        AppFontWeight.Medium -> FontWeight.Medium
        AppFontWeight.Bold -> FontWeight.Bold
    }