package io.github.lycosmic.lithe.presentation.reader.model

import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

/**
 * 阅读器视觉样式
 */
data class ReaderStyle(
    // 核心颜色
    val backgroundColor: Color,
    val textColor: Color,

    // 字体排版
    val fontFamily: FontFamily,
    val fontSize: TextUnit,
    val fontWeight: FontWeight,
    val fontStyle: FontStyle,
    val lineHeight: TextUnit,
    val letterSpacing: TextUnit,
    val paragraphIndent: TextUnit,
    val paragraphSpacing: Dp,

    // 对齐方式
    val titleAlign: TextAlign,
    val paragraphAlign: TextAlign, // 视觉上的对齐
    val paragraphHorizontalAlign: Alignment.Horizontal, // 布局上的对齐

    // 图片设置
    val imageVisible: Boolean,
    val imageCaptionVisible: Boolean,
    val imageAlign: Alignment,
    val imageWidthPercent: Float,
    val imageCornerRadius: Dp,
    val imageColorFilter: ColorFilter?,

    // 边距
    val sidePadding: Dp
)