package io.github.lycosmic.lithe.data.parser.content.txt

import android.net.Uri
import io.github.lycosmic.lithe.data.model.ReaderContent
import io.github.lycosmic.lithe.data.parser.content.BookContentParser
import io.github.lycosmic.lithe.extension.logE
import javax.inject.Inject
import javax.inject.Singleton


/**
 * TXT 书籍内容解析器
 */
@Singleton
class TxtContentParser @Inject constructor() : BookContentParser {
    override suspend fun parseChapterContent(
        bookUri: Uri,
        chapterPathOrHref: String
    ): List<ReaderContent> {
        return emptyList()
    }


    /**
     * 解析 TXT 目录项的链接
     */
    private fun parseTxtHref(href: String): Pair<Long, Long>? {
        try {
            val parts = href.removePrefix("txt://").split(",")
            val start = parts[0].substringAfter("start:").toLong()
            val end = parts[1].substringAfter("end:").toLong()
            return start to end
        } catch (e: Exception) {
            logE(e = e) {
                "解析 TXT 目录项的链接失败: $href"
            }
            return null
        }
    }
}