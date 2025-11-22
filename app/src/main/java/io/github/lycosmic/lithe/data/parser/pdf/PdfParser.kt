package io.github.lycosmic.lithe.data.parser.pdf

import android.content.Context
import android.net.Uri
import io.github.lycosmic.lithe.data.model.ParsedMetadata
import io.github.lycosmic.lithe.data.parser.BookParser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfParser @Inject constructor() : BookParser {
    override suspend fun parse(
        context: Context,
        uri: Uri
    ): ParsedMetadata {
        // TODO 使用文档第一页作为封面

        // 使用文件名作为书名
        val fileName = uri.lastPathSegment

        return ParsedMetadata(
            title = fileName.toString(),
            author = "",
            description = "PDF 文档",
            coverPath = null
        )
    }
}