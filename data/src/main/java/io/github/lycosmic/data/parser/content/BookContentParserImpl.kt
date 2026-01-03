package io.github.lycosmic.data.parser.content

import io.github.lycosmic.domain.model.BookChapter
import io.github.lycosmic.domain.model.BookContentBlock
import io.github.lycosmic.domain.model.FileFormat
import io.github.lycosmic.domain.repository.BookContentParser
import javax.inject.Inject

class BookContentParserImpl @Inject constructor(
    private val bookContentParserFactory: BookContentParserFactory
) : BookContentParser {

    override suspend fun parseChapter(
        uriString: String,
        bookId: Long,
        format: FileFormat
    ): Result<List<BookChapter>> {
        val parser = bookContentParserFactory.getParser(format)
            ?: return Result.failure(Exception("不支持解析章节的文件格式：$format"))

        return try {
            val chapters = parser.parseChapter(bookId, uriString)
            if (chapters.isEmpty()) {
                return Result.failure(Exception("解析章节失败，未找到任何章节"))
            }
            return Result.success(chapters)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loadChapterContent(
        uriString: String,
        format: FileFormat,
        chapter: BookChapter
    ): Result<List<BookContentBlock>> {
        val parser = bookContentParserFactory.getParser(format)
            ?: return Result.failure(Exception("不支持解析章节内容的文件格式：$format"))

        return try {
            val contents = parser.loadChapter(uriString, chapter)
            return Result.success(contents)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}