package io.github.lycosmic.data.parser.metadata.pdf

import androidx.core.net.toUri
import io.github.lycosmic.data.parser.metadata.MetadataParserStrategy
import io.github.lycosmic.model.BookMetadata
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfMetadataParser @Inject constructor(
) : MetadataParserStrategy {
    override suspend fun parse(uriString: String): BookMetadata {
        // TODO 使用文档第一页作为封面

        val uri = uriString.toUri()
        // 使用文件名作为书名
        val fileName = uri.lastPathSegment

        return BookMetadata(
            title = fileName.toString(),
            authors = listOf("未知作者"),
            description = "PDF 文档",
            coverPath = null
        )
    }
}