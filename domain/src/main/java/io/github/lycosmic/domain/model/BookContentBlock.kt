package io.github.lycosmic.domain.model

/**
 * 书籍内容块
 */
sealed interface BookContentBlock {

    // 起始字符索引
    val startIndex: Int

    /**
     * 标题
     */
    data class Title(val text: String, val level: Int, override val startIndex: Int) :
        BookContentBlock

    /**
     * 段落
     */
    data class Paragraph(
        val text: String,
        val styles: List<StyleRange>,
        override val startIndex: Int
    ) : BookContentBlock

    /**
     * 分割线
     */
    data class Divider(override val startIndex: Int) : BookContentBlock

    /**
     * 图片
     */
    data class Image(val path: String, override val startIndex: Int, val aspectRatio: Float) :
        BookContentBlock

    /**
     * 图片描述
     */
    data class ImageAlt(val caption: String, override val startIndex: Int) : BookContentBlock
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