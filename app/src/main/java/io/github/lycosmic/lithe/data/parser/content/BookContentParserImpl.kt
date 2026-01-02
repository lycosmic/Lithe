package io.github.lycosmic.lithe.data.parser.content

import io.github.lycosmic.model.BookContentBlock
import io.github.lycosmic.model.BookSpineItem
import io.github.lycosmic.model.FileFormat
import io.github.lycosmic.repository.BookContentParser
import javax.inject.Inject

class BookContentParserImpl @Inject constructor(
    private val bookContentParserFactory: BookContentParserFactory
) : BookContentParser {

    override suspend fun parseSpine(
        uriString: String,
        format: FileFormat
    ): Result<List<BookSpineItem>> {
        val parser = bookContentParserFactory.getParser(format)
            ?: return Result.failure(Exception("不支持解析章节的文件格式：$format"))


        return try {
            val spineItems = parser.parseSpine(uriString)
            return Result.success(spineItems)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loadChapterContent(
        bookUriString: String,
        format: FileFormat,
        spineItem: BookSpineItem
    ): Result<List<BookContentBlock>> {
        val parser = bookContentParserFactory.getParser(format)
            ?: return Result.failure(Exception("不支持解析章节内容的文件格式：$format"))

        return try {
            val contents = parser.loadChapter(bookUriString, spineItem)
            return Result.success(contents)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


}