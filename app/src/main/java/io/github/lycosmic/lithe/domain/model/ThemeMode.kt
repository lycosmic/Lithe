package io.github.lycosmic.lithe.domain.model

/**
 * 主题模式
 */
enum class ThemeMode(val value: String) {
    LIGHT("light"),
    DARK("dark"),
    SYSTEM("system");

    companion object {
        /**
         * 从字符串值获取主题模式
         */
        fun fromValue(value: String): ThemeMode {
            return entries.find { it.value == value } ?: SYSTEM
        }
    }
}