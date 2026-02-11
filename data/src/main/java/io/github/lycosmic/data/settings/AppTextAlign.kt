package io.github.lycosmic.data.settings

import androidx.compose.ui.text.style.TextAlign

/**
 * 阅读器文本对齐方式
 */
enum class AppTextAlign {
    START,      // 开头 (左对齐/起始对齐)
    JUSTIFY,    // 文本对齐 (两端对齐)
    CENTER,     // 居中
    END;        // 末尾 (右对齐/结束对齐)

    companion object {
        fun getTextAlign(textAlign: AppTextAlign): TextAlign {
            return when (textAlign) {
                START -> TextAlign.Start
                JUSTIFY -> TextAlign.Justify
                CENTER -> TextAlign.Center
                END -> TextAlign.End
            }
        }
    }
}