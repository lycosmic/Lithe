package io.github.lycosmic.lithe.data.model

import androidx.compose.ui.text.AnnotatedString

/**
 * 书籍具体可阅读的内容
 */
sealed interface ReaderContent {

    // 标题
    data class Title(
        val text: String,
        val level: Int = 1 // 标题级别
    ) : ReaderContent

    // 段落
    data class Paragraph(
        val text: AnnotatedString // 段落内容
    ) : ReaderContent

    // 图片
    data class Image(
        val path: String, // 图片路径
        val caption: String? = null // 图片标题
    ) : ReaderContent

    // 分割线
    data object Divider : ReaderContent
}