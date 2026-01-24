package io.github.lycosmic.domain.repository

import io.github.lycosmic.domain.model.BookChapter

/**
 * 书籍章节仓库
 */
interface ChapterRepository {

    /**
     * 通过书籍ID获取章节列表
     */
    suspend fun getChaptersByBookId(bookId: Long): Result<List<BookChapter>>

    /**
     * 保存章节列表
     */
    suspend fun saveChapters(spine: List<BookChapter>): Result<List<Long>>

    /**
     * 根据书籍ID删除章节列表
     */
    suspend fun deleteChaptersByBookId(bookId: Long): Result<Unit>
}