package io.github.lycosmic.domain.model


/**
 * 书籍阅读进度
 */
data class ReadingProgress(
    val chapterIndex: Int, // 当前章节
    val chapterOffset: Float, // 章节进度
    val totalProgress: Float // 书籍总进度
) {
    companion object {
        const val KEY_CHAPTER_INDEX = "chapter_index"
        const val KEY_CHAPTER_OFFSET = "chapter_offset"
        const val KEY_TOTAL_PROGRESS = "total_progress"
    }
}