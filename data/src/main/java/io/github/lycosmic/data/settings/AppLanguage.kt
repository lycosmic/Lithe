package io.github.lycosmic.data.settings

/**
 * 应用语言
 */
enum class AppLanguage(
    val label: String, // 显示的名称
    val code: String // Locale 代码
) {
    CHINESE("简体中文", "zh-CN"),
    ENGLISH("English", "en");

    companion object {
        fun fromCode(code: String?, default: AppLanguage = CHINESE): AppLanguage {
            return entries.find { it.code == code } ?: default
        }
    }
}