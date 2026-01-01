package io.github.lycosmic.lithe.data.parser.metadata.pdf

import android.net.Uri
import io.github.lycosmic.lithe.data.parser.metadata.BookMetadataParser
import io.github.lycosmic.lithe.domain.model.ParsedMetadata
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfMetadataParser @Inject constructor() : BookMetadataParser {
    override suspend fun parse(
        uri: Uri
    ): ParsedMetadata {
        // TODO 使用文档第一页作为封面

        // 使用文件名作为书名
        val fileName = uri.lastPathSegment

        return ParsedMetadata(
            title = fileName.toString(),
            authors = listOf("未知作者"),
            description = "PDF 文档",
            coverPath = null
        )
    }
}