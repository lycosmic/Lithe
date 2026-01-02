package io.github.lycosmic.repository

import io.github.lycosmic.model.BookContentBlock
import io.github.lycosmic.model.BookSpineItem
import io.github.lycosmic.model.FileFormat

interface BookContentParser {
    /**
     * 解析目录结构
     */
    suspend fun parseSpine(uriString: String, format: FileFormat): Result<List<BookSpineItem>>

    /**
     * 加载具体章节的内容
     */
    suspend fun loadChapterContent(
        bookUriString: String,
        format: FileFormat,
        spineItem: BookSpineItem,
    ): Result<List<BookContentBlock>>
}