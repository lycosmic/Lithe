package io.github.lycosmic.lithe.data.parser.content

import android.net.Uri
import io.github.lycosmic.lithe.data.model.ReaderContent

interface BookContentParser {

    /**
     * 解析当前章节的内容
     */
    suspend fun parseChapterContent(
        bookUri: Uri,
        chapterHref: String
    ): List<ReaderContent>
}