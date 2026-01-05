package io.github.lycosmic.lithe.presentation.reader.model

import androidx.compose.ui.text.AnnotatedString

/**
 * 书籍具体可阅读的内容
 */
sealed interface ReaderContent {
    // 内容在当前章节中的起始字符位置
    val startIndex: Int

    // 当前内容所属的章节索引
    val chapterIndex: Int

    // 标题
    data class Title(
        override val startIndex: Int,
        override val chapterIndex: Int,
        val text: String,
        val level: Int = 1,  // 标题级别
    ) : ReaderContent

    // 段落
    data class Paragraph(
        override val startIndex: Int,
        override val chapterIndex: Int,
        val text: AnnotatedString // 段落内容
    ) : ReaderContent

    // 图片
    data class Image(
        override val startIndex: Int, // 图片通常占用一个位置
        override val chapterIndex: Int,
        val path: String, // 图片路径
        val caption: String? = null // 图片标题
    ) : ReaderContent

    // 分割线
    data class Divider(override val startIndex: Int, override val chapterIndex: Int) :
        ReaderContent
}