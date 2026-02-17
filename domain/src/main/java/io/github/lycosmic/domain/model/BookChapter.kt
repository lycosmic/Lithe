package io.github.lycosmic.domain.model

import kotlinx.serialization.Serializable


/**
 * 书籍章节
 */
@Serializable
sealed class BookChapter(
    open val bookId: Long,
    open val index: Int,
    open val title: String,
    open val fileSizeBytes: Long, // 章节原始文件字节大小，方便实现粗略进度且不牺牲秒开的性能（不预读全文）
    // 真实字符数，-1L 表示还未开始计算
    open val realCharCount: Long = -1L
) {

    /**
     * EPUB书籍章节
     */
    data class EpubChapter(
        val href: String,
        override val bookId: Long,
        override val index: Int,
        override val title: String,
        override val fileSizeBytes: Long,  // 章节文件完整ZIP相对路径
        override val realCharCount: Long = -1L
    ) : BookChapter(bookId, index, title, fileSizeBytes)


    /**
     * TXT书籍章节
     */
    data class TxtChapter(
        override val bookId: Long,
        override val index: Int,
        override val title: String,
        val startOffset: Long, // 文件的字节起始偏移量
        val endOffset: Long,  // 文件的字节结束偏移量
        override val fileSizeBytes: Long = endOffset - startOffset,
        override val realCharCount: Long = -1L
    ) : BookChapter(bookId, index, title, fileSizeBytes)


    // 判断当前是否是精确模式
    val isPrecise: Boolean get() = realCharCount != -1L

    // 获取用于计算进度的有效长度
    val effectiveLength: Long get() = if (isPrecise) realCharCount else fileSizeBytes
}



