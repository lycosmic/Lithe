package io.github.lycosmic.repository

import io.github.lycosmic.model.BookChapter

/**
 * 书籍章节仓库
 */
interface ChapterRepository {

    /**
     * 通过书籍ID获取章节列表
     */
    suspend fun getChaptersByBookId(bookId: Long): List<BookChapter>

    /**
     * 保存章节列表
     */
    suspend fun saveChapters(bookId: Long, spine: List<BookChapter>)
}