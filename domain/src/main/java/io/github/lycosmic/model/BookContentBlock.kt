package io.github.lycosmic.model

/**
 * 书籍内容块
 */
sealed interface BookContentBlock {
    /**
     * 标题
     */
    data class Title(val text: String, val level: Int) : BookContentBlock

    /**
     * 段落
     */
    data class Paragraph(val text: String, val styles: List<StyleRange>) : BookContentBlock

    /**
     * 分割线
     */
    data object Divider : BookContentBlock

    /**
     * 图片
     */
    data class Image(val path: String) : BookContentBlock
}


/**
 * 样式范围
 */
data class StyleRange(val start: Int, val end: Int, val type: StyleType)

/**
 * 样式类型
 */
enum class StyleType {
    // 加粗
    BOLD,

    // 斜体
    ITALIC,
}