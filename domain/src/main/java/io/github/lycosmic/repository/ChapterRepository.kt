package io.github.lycosmic.repository

import io.github.lycosmic.model.BookSpineItem

/**
 * 书籍章节仓库
 */
interface ChapterRepository {

    /**
     * 通过书籍ID获取章节列表
     */
    suspend fun getChaptersByBookId(bookId: Long): List<BookSpineItem>

    /**
     * 保存章节列表
     */
    suspend fun saveChapters(bookId: Long, spine: List<BookSpineItem>)
}