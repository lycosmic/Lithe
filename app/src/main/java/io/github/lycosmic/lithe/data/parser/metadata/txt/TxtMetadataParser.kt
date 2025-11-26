package io.github.lycosmic.lithe.data.parser.metadata.txt

import android.content.Context
import android.net.Uri
import io.github.lycosmic.lithe.data.model.ParsedMetadata
import io.github.lycosmic.lithe.data.parser.metadata.BookMetadataParser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TxtMetadataParser @Inject constructor() : BookMetadataParser {
    override suspend fun parse(
        context: Context,
        uri: Uri
    ): ParsedMetadata {
        return ParsedMetadata(
            title = "文本文件",
            author = "未知",
            description = "纯文本文件",
        )
    }
}