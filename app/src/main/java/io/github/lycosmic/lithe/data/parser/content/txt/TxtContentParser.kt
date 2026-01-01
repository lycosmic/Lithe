package io.github.lycosmic.lithe.data.parser.content.txt

import android.content.Context
import android.net.Uri
import androidx.compose.ui.text.AnnotatedString
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.lycosmic.lithe.data.parser.content.BookContentParser
import io.github.lycosmic.lithe.domain.model.ReaderContent
import io.github.lycosmic.lithe.extension.logE
import io.github.lycosmic.lithe.utils.CharsetUtils
import java.io.BufferedReader
import java.io.InputStreamReader
import javax.inject.Inject
import javax.inject.Singleton


/**
 * TXT 书籍内容解析器
 */
@Singleton
class TxtContentParser @Inject constructor(
    @param:ApplicationContext
    private val context: Context
) : BookContentParser {
    override suspend fun parseChapterContent(
        bookUri: Uri,
        chapterPathOrHref: String
    ): List<ReaderContent> {
        val contentList = mutableListOf<ReaderContent>()

        // 解析目录链接，拿到字符的起始位置和结束位置
        val (start, end) = parseTxtHref(chapterPathOrHref) ?: return contentList
        val lengthToRead = (end - start).toInt()

        if (lengthToRead <= 0) {
            return contentList
        }

        context.contentResolver.openInputStream(bookUri)?.use { inputStream ->
            val charset = CharsetUtils.detectCharset(inputStream)
            // 需要重新打开以确保从头开始 skip
            context.contentResolver.openInputStream(bookUri)?.use { properStream ->
                val reader = BufferedReader(InputStreamReader(properStream, charset))

                // 跳过前面的字符
                reader.skip(start)

                // 读取指定长度的内容
                val buffer = CharArray(lengthToRead)
                val read = reader.read(buffer, 0, lengthToRead)

                if (read > 0) {
                    val rawText = String(buffer, 0, read)

                    // 按换行符切割为段落
                    val lines = rawText.split("\n")

                    // 第一行通常是标题，做个特殊处理
                    if (lines.isNotEmpty()) {
                        val title = lines[0].trim()
                        if (title.isNotEmpty()) {
                            contentList.add(ReaderContent.Title(title))
                        }
                    }

                    // 其余行是段落
                    for (i in 1 until lines.size) {
                        val paragraph = lines[i].trim()
                        // 过滤掉空行，或者只包含空格的行
                        if (paragraph.isNotEmpty()) {
                            // 处理 TXT 常见的段首缩进 (全角空格)
                            val cleanText = paragraph.replace("\u3000", "  ")
                            contentList.add(ReaderContent.Paragraph(AnnotatedString(cleanText)))
                        }
                    }
                }
            }
        }

        return contentList
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