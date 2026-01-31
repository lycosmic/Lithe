package io.github.lycosmic.domain.model

/**
 * 字体系列模型
 * @param id 字体唯一标识 (也是 Google Fonts 的 ID)
 * @param displayName 显示名称
 * @param family 具体的 FontFamily 对象 (若已缓存则为本地字体，否则为系统)
 */
data class MyFontFamily(
    val id: String,
    val displayName: String,
    val family: Any? = null
) {
    companion object {
        val Default = MyFontFamily(
            id = "system",
            displayName = "",
            family = null
        )
    }
}