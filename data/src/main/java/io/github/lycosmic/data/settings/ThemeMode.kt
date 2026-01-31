package io.github.lycosmic.data.settings


/**
 * 主题模式
 */
enum class ThemeMode(val value: String) {
    LIGHT("light"),
    DARK("dark"),
    SYSTEM("system");

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