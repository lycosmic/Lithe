package io.github.lycosmic.domain.model

import kotlinx.serialization.Serializable


/**
 * 书籍阅读进度
 */
@Serializable
data class ReadingProgress(
    val bookId: Long, // 书籍ID
    val chapterIndex: Int, // 章节索引
    val chapterOffsetCharIndex: Int, // 当前章节的字符索引：指向当前屏幕顶部第一个可见字符在全文中的位置
    val progressPercent: Float, // 全书阅读进度百分比
    val lastReadTime: Long = System.currentTimeMillis(), // 最后阅读时间
    // --- UI 性能优化数据 ---
    /**
     * LazyColumn 的 item 索引
     */
    val uiItemIndex: Int = 0,

    /**
     * LazyColumn 的 item 内部偏移量
     */
    val uiItemOffset: Int = 0,
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