package io.github.lycosmic.data.settings


const val LIGHT_VALUE = "light"
const val DARK_VALUE = "dark"
const val SYSTEM_VALUE = "system"

/**
 * 主题模式
 */
enum class ThemeMode(val value: String) {
    LIGHT(LIGHT_VALUE),
    DARK(DARK_VALUE),
    SYSTEM(SYSTEM_VALUE);

    companion object {
        /**
         * 默认主题模式
         */
        val DEFAULT_THEME_MODE = SYSTEM

        /**
         * 从字符串值获取主题模式
         */
        fun fromValue(value: String): ThemeMode {
            return entries.find { it.value == value } ?: DEFAULT_THEME_MODE
        }
    }
}