package io.github.lycosmic.data.settings


/**
 * 阅读器图片颜色效果
 */
enum class ImageColorEffect {
    NONE, // 无效果
    GRAYSCALE, // 黑白/灰度
    FONT_COLOR, // 文字颜色
    BACKGROUND_COLOR; // 背景颜色

    companion object {
        fun fromName(name: String): ImageColorEffect {
            return entries.find { it.name == name } ?: NONE
        }
    }
}