package io.github.lycosmic.domain.model

import kotlinx.serialization.Serializable


/**
 * 书籍章节
 */
@Serializable
sealed class BookChapter {
    abstract val bookId: Long
    abstract val index: Int
    abstract val title: String
}

/**
 * EPUB书籍章节
 */
@Serializable
data class EpubChapter(
    override val bookId: Long,
    override val index: Int,
    override val title: String,
    val href: String // 章节文件的Zip相对路径
) : BookChapter()

/**
 * TXT书籍章节
 */
@Serializable
data class TxtChapter(
    override val bookId: Long,
    override val index: Int,
    override val title: String,
    val startOffset: Long, // 文件的字节起始偏移量
    val endOffset: Long // 文件的字节结束偏移量
) : BookChapter()




