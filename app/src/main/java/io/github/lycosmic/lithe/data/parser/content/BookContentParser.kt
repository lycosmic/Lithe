package io.github.lycosmic.lithe.data.parser.content

import android.net.Uri
import io.github.lycosmic.lithe.domain.model.ReaderContent

interface BookContentParser {

    /**
     * 解析当前章节的内容
     */
    suspend fun parseChapterContent(
        bookUri: Uri,
        chapterPathOrHref: String // 对于 EPUB 书籍，表示章节的路径，对于 TXT 书籍，表示章节的位置信息
    ): List<ReaderContent>
}