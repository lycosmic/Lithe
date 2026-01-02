package io.github.lycosmic.model


/**
 * 颜色预设
 */
data class ColorPreset(
    val id: Long,
    val name: String,
    val backgroundColorArgb: Int,
    val textColorArgb: Int,
    val isSelected: Boolean = false
) {
    companion object {
        // 默认 ID
        const val DEFAULT_ID = -1L

        // 默认名称
        const val DEFAULT_NAME = ""

        // 默认背景颜色
        const val DEFAULT_BACKGROUND_COLOR_ARGB = 0xFFFAF8FF.toInt()

        // 默认文字颜色
        const val DEFAULT_TEXT_COLOR_ARGB = 0xFF44464F.toInt()

        val defaultColorPreset = ColorPreset(
            id = DEFAULT_ID,
            name = DEFAULT_NAME,
            backgroundColorArgb = DEFAULT_BACKGROUND_COLOR_ARGB,
            textColorArgb = DEFAULT_TEXT_COLOR_ARGB
        )
    }
}