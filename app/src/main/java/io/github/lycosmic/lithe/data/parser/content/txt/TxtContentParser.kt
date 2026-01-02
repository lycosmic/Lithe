package io.github.lycosmic.lithe.data.parser.content.txt

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.lycosmic.lithe.data.parser.content.ContentParserStrategy
import io.github.lycosmic.lithe.log.logE
import io.github.lycosmic.lithe.util.CharsetUtils
import io.github.lycosmic.model.BookContentBlock
import io.github.lycosmic.model.BookSpineItem
import io.github.lycosmic.model.TxtSpineItem
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton


/**
 * TXT 书籍内容解析器
 */
@Singleton
class TxtContentParser @Inject constructor(
    @param:ApplicationContext
    private val context: Context
) : ContentParserStrategy {
    override suspend fun parseSpine(uri: String): List<BookSpineItem> {
        return parseSpine(context, uri.toUri())
    }

    override suspend fun loadChapter(
        uri: String,
        item: BookSpineItem
    ): List<BookContentBlock> {

        val spineHref = (item as TxtSpineItem).href
        val bookUri = uri.toUri()

        val contentList = mutableListOf<BookContentBlock>()

        // 解析目录链接，拿到字符的起始位置和结束位置
        val (start, end) = parseTxtHref(spineHref) ?: return contentList
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
                            contentList.add(BookContentBlock.Title(title, 0))
                        }
                    }

                    // 其余行是段落
                    for (i in 1 until lines.size) {
                        val paragraph = lines[i].trim()
                        // 过滤掉空行，或者只包含空格的行
                        if (paragraph.isNotEmpty()) {
                            // 处理 TXT 常见的段首缩进 (全角空格)
                            val cleanText = paragraph.replace("\u3000", "  ")
                            contentList.add(BookContentBlock.Paragraph(cleanText, emptyList()))
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


    /**
     * 解析 TXT 目录 (虚拟目录)
     * 策略：流式扫描全书，记录章节标题和它的字符偏移量
     */
    private fun parseSpine(context: Context, uri: Uri): List<TxtSpineItem> {
        val spine = mutableListOf<TxtSpineItem>()

        // 默认的章节标题
        var defaultChapterTitle = ""

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            // 检测编码
            val charset = CharsetUtils.detectCharset(inputStream)

            // 重新打开流进行读取
            context.contentResolver.openInputStream(uri)?.use { properStream ->
                val reader = BufferedReader(InputStreamReader(properStream, charset))

                // 当前读取到的字符位置
                var charCount = 0L
                var lastChapterTitle = ""
                var lastChapterStart = 0L
                var chapterOrder = 1
                var isFirstChapter = true

                // 预先添加第一章，防止前面的内容没有标题
                reader.forEachLine { line ->
                    if (isFirstChapter && line.isNotBlank()) {
                        lastChapterTitle = line.trim()
                        defaultChapterTitle = lastChapterTitle
                        isFirstChapter = false
                    }

                    // 包含换行符长度 1
                    val lineLength = line.length + 1


                    // 匹配正则
                    if (chapterPattern.matcher(line).matches()) {
                        // 找到新章节，结算上一章
                        val href = makeTxtHref(lastChapterStart, charCount)
                        spine.add(
                            TxtSpineItem(
                                id = lastChapterStart.toString(),
                                href = href,
                                label = lastChapterTitle,
                                order = chapterOrder
                            )
                        )

                        // 开启新一章
                        lastChapterTitle = line.trim()
                        lastChapterStart = charCount
                    }

                    chapterOrder++
                    charCount += lineLength
                }

                // 结算最后一章
                val lastHref = makeTxtHref(lastChapterStart, charCount)
                spine.add(
                    TxtSpineItem(
                        id = lastChapterStart.toString(),
                        href = lastHref,
                        label = lastChapterTitle,
                        order = chapterOrder
                    )
                )
            }
        }

        // 如果全书没匹配到任何章节，就把它当成只有一章
        if (spine.isEmpty()) {
            // 获取文件总长度
            val totalLen = getFileLength(context, uri)
            spine.add(
                TxtSpineItem(
                    label = defaultChapterTitle,
                    href = makeTxtHref(0, totalLen),
                    id = uri.toString(),
                    order = 1
                )
            )
        }

        return spine
    }

    // 章节正则
    private val chapterPattern = Pattern.compile(
        "^\\s*(?:第|Chapter)\\s*[0-9零一二三四五六七八九十百千]+\\s*(?:章|节|卷|Section|Part).*",
        Pattern.CASE_INSENSITIVE
    )

    /**
     * 生成 TXT 目录项的链接
     */
    private fun makeTxtHref(start: Long, end: Long): String {
        return "txt://start:$start,end:$end"
    }

    /**
     * 获取文件长度
     */
    private fun getFileLength(context: Context, uri: Uri): Long {
        return context.contentResolver.openFileDescriptor(uri, "r")?.use {
            it.statSize
        } ?: 0L
    }


}