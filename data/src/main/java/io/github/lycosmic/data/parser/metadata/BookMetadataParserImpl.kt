package io.github.lycosmic.data.parser.metadata

import io.github.lycosmic.model.BookMetadata
import io.github.lycosmic.model.FileFormat
import io.github.lycosmic.repository.BookMetadataParser
import javax.inject.Inject

class BookMetadataParserImpl @Inject constructor(
    private val bookMetadataParserFactory: BookMetadataParserFactory
) : BookMetadataParser {
    override suspend fun parse(
        uriString: String,
        format: FileFormat
    ): Result<BookMetadata> {
        // 获取元数据解析器
        val parser = bookMetadataParserFactory.getMetadataParser(format)
            ?: return Result.failure(Exception("不支持的文件格式：$format"))

        return try {
            // 解析元数据
            val metadata = parser.parse(uriString)
            return Result.success(metadata)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}