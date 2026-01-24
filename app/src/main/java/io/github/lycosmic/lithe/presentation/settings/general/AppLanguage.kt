package io.github.lycosmic.lithe.presentation.settings.general

/**
 * 应用语言
 */
data class AppLanguage(
    val name: String, // 显示的名称
    val code: String // Locale 代码
) {
    companion object {
        // 支持的语言
        val SUPPORTED_LANGUAGES = listOf(
            AppLanguage("简体中文", "zh-CN"),
            AppLanguage("English", "en"),
        )

        const val DEFAULT_LANGUAGE_CODE = "zh-CN"
    }
}