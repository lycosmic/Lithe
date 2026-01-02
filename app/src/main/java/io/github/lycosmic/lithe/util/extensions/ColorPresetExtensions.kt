package io.github.lycosmic.lithe.util.extensions

import androidx.compose.ui.graphics.Color
import io.github.lycosmic.model.ColorPreset

/**
 * 将颜色预设中的颜色值转换为 Compose 颜色
 */

val ColorPreset.backgroundColor: Color
    get() = Color(this.backgroundColorArgb)

val ColorPreset.textColor: Color
    get() = Color(this.textColorArgb)