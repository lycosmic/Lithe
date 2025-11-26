package io.github.lycosmic.lithe.data.parser.content

import android.content.Context
import android.net.Uri
import io.github.lycosmic.lithe.data.model.content.ReaderContent

interface BookContentParser {

    /**
     * 解析当前章节的内容
     */
    suspend fun parseChapterContent(
        context: Context,
        bookUri: Uri,
        chapterHref: String
    ): List<ReaderContent>
}