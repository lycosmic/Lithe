package io.github.lycosmic.domain.model


/**
 * 书籍阅读进度
 */
data class ReadingProgress(
    val bookId: Long, // 书籍ID
    val chapterIndex: Int, // 当前章节的索引
    val chapterOffsetCharIndex: Int, // 字符索引
    val progressPercent: Float, // 进度百分比
) {
    companion object {
        fun default(bookId: Long): ReadingProgress {
            return ReadingProgress(
                bookId = bookId,
                chapterIndex = 0,
                chapterOffsetCharIndex = 0,
                progressPercent = 0f
            )
        }
    }
}