package io.github.lycosmic.data.parser.content.txt

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.lycosmic.data.parser.content.ContentParserStrategy
import io.github.lycosmic.data.util.CharsetHelper
import io.github.lycosmic.data.util.CountingInputStream
import io.github.lycosmic.data.util.getFileSize
import io.github.lycosmic.model.BookChapter
import io.github.lycosmic.model.BookContentBlock
import io.github.lycosmic.model.TxtChapter
import io.github.lycosmic.util.DomainLogger
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
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
    private val context: Context,
    private val logger: DomainLogger
) : ContentParserStrategy {
    override suspend fun parseChapter(bookId: Long, uriString: String): List<BookChapter> {
        return parseSpine(context, uriString.toUri(), bookId)
    }

    // 章节正则
    private val chapterPattern = Pattern.compile(
        "^\\s*(?:第|Chapter)\\s*[0-9零一二三四五六七八九十百千]+\\s*(?:章|节|卷|Section|Part).*",
        Pattern.CASE_INSENSITIVE
    )

    /**
     * 解析 TXT 章节 (虚拟章节)
     * 策略：流式扫描全书，记录章节标题和它的字节偏移量
     */
    private fun parseSpine(context: Context, uri: Uri, bookId: Long): List<TxtChapter> {
        val chapters = mutableListOf<TxtChapter>()
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            // 检测编码
            val charset = CharsetHelper.detectCharset(inputStream)

            // 重新打开流进行读取
            context.contentResolver.openInputStream(uri)?.use { rawStream ->
                // 计数流
                val countingStream = CountingInputStream(rawStream)
                // 读取的字节数组
                val baos = ByteArrayOutputStream()

                // 使用缓冲流包装进行预读
                val bis = countingStream.buffered()

                // 是否为第一章
                var isFirstChapter = true

                // 章节序号
                var chapterOrder = 0

                // 上一章的标题和起始位置
                var lastChapterTitle = ""
                var lastChapterStartOffset = 0L
                var lastOffsetOfLineContent = 0L

                while (true) {
                    val byte = bis.read()
                    if (byte == -1) {
                        break
                    }

                    // 是否是一行结束
                    var isLineEnd = false
                    if (byte == '\n'.code) {
                        // LF (\n)
                        isLineEnd = true
                    } else if (byte == '\r'.code) {
                        // CRLF (\r\n) 或 CR (\r)
                        isLineEnd = true

                        // 预读下一个字节
                        bis.mark(1)
                        val nextByte = bis.read()
                        if (nextByte != '\n'.code) {
                            // 不是 \r\n，退回预读的字节
                            bis.reset()
                        }
                    } else {
                        // 写入ByteArrayOutputStream
                        baos.write(byte)
                        lastOffsetOfLineContent = countingStream.getCount()
                    }


                    if (isLineEnd) {
                        // 行字符串
                        val line = baos.toString(charset.name()).trim()
                        // 当前行末尾的字节偏移
                        val currentLineEndOffset = countingStream.getCount()

                        // 预先设置标题，防止前面的内容没有标题
                        if (isFirstChapter && line.isNotBlank()) {
                            lastChapterTitle = line
                            isFirstChapter = false
                        }

                        // 匹配正则
                        if (chapterPattern.matcher(line).matches()) {
                            // 找到新章节，结算上一章
                            // 当前行的字节长度 = 行字符串的字节长度 + 换行符字节长度
                            val lineLength =
                                line.toByteArray(charset).size + (currentLineEndOffset - lastOffsetOfLineContent)
                            val endOffset = currentLineEndOffset - lineLength
                            chapters.add(
                                TxtChapter(
                                    bookId = bookId,
                                    index = chapterOrder,
                                    title = lastChapterTitle,
                                    startOffset = lastChapterStartOffset,
                                    endOffset = endOffset
                                )
                            )

                            // 开启新的一章
                            lastChapterTitle = line.trim()
                            lastChapterStartOffset = endOffset
                            chapterOrder++
                        }

                        baos.reset()
                    }
                }

                // 结算最后一章
                // 如果没有匹配到任何章节，也会结算唯一的一章
                uri.getFileSize(context).onSuccess { totalSize ->
                    chapters.add(
                        TxtChapter(
                            bookId = bookId,
                            index = chapterOrder,
                            title = lastChapterTitle,
                            startOffset = lastChapterStartOffset,
                            endOffset = totalSize
                        )
                    )
                }.onFailure {
                    logger.e {
                        "解析TXT书籍章节信息时，无法获取文件长度"
                    }
                }
            }
        }

        return chapters
    }


    override suspend fun loadChapter(
        uriString: String,
        chapter: BookChapter
    ): List<BookContentBlock> {
        // 字节的起始位置和结束位置
        val start = (chapter as? TxtChapter)?.startOffset ?: return emptyList()
        val end = chapter.endOffset
        val lengthToRead = (end - start).toInt()

        val bookUri = uriString.toUri()
        val contentList = mutableListOf<BookContentBlock>()

        if (lengthToRead <= 0) {
            return contentList
        }

        context.contentResolver.openInputStream(bookUri)?.use { inputStream ->
            val charset = CharsetHelper.detectCharset(inputStream)

            // 需要重新打开以确保从头开始 skip
            context.contentResolver.openInputStream(bookUri)?.use { rawInputStream ->
                // 跳过前面的字节
                val skipped = rawInputStream.skip(start)
                if (skipped == start) {
                    // 定位成功，开始读取内容
                    val reader = InputStreamReader(rawInputStream, charset)
                    val bufferedReader = BufferedReader(reader)

                    // 读取指定长度的内容
                    val buffer = CharArray(lengthToRead)
                    val read = bufferedReader.read(buffer, 0, lengthToRead)

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
                } else {
                    logger.e {
                        "跳转章节时，跳转位置超出文件长度"
                    }
                }
            }
        }

        return contentList
    }


}