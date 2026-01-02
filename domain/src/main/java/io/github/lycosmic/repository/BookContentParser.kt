package io.github.lycosmic.repository

import io.github.lycosmic.model.BookChapter
import io.github.lycosmic.model.BookContentBlock
import io.github.lycosmic.model.FileFormat

interface BookContentParser {
    /**
     * 解析目录结构
     */
    suspend fun parseChapter(
        uriString: String,
        bookId: Long,
        format: FileFormat
    ): Result<List<BookChapter>>

    /**
     * 加载具体章节的内容
     */
    suspend fun loadChapterContent(
        uriString: String,
        format: FileFormat,
        chapter: BookChapter,
    ): Result<List<BookContentBlock>>
}