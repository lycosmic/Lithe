package io.github.lycosmic.lithe.data.parser.content

import io.github.lycosmic.model.BookChapter
import io.github.lycosmic.model.BookContentBlock

/**
 * 内容解析策略接口
 */
interface ContentParserStrategy {
    /**
     * 解析目录结构
     * @param uri 文件路径
     * @return 目录结构
     */
    suspend fun parseChapter(bookId: Long, uriString: String): List<BookChapter>

    /**
     * 加载具体章节的内容
     * @param uri 文件路径
     * @param item 目录项
     * @return 章节内容
     */
    suspend fun loadChapter(uriString: String, chapter: BookChapter): List<BookContentBlock>
}