package io.github.lycosmic.lithe.data.parser.txt

import android.content.Context
import android.net.Uri
import io.github.lycosmic.lithe.data.model.ParsedMetadata
import io.github.lycosmic.lithe.data.parser.BookParser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TxtParser @Inject constructor() : BookParser {
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