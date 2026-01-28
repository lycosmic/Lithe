package io.github.lycosmic.data.parser.content.txt

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.lycosmic.data.parser.content.ContentParserStrategy
import io.github.lycosmic.data.util.CharsetHelper
import io.github.lycosmic.data.util.getFileSize
import io.github.lycosmic.data.util.skipFully
import io.github.lycosmic.domain.model.BookChapter
import io.github.lycosmic.domain.model.BookContentBlock
import io.github.lycosmic.domain.model.TxtChapter
import io.github.lycosmic.domain.util.DomainLogger
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
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

    /**
     * 匹配章节的正则
     * 匹配规则：
     * 1. \s* 匹配0个或多个空白字符
     * 2. [...]+ 匹配1个或多个代表数字的字符
     * 3. .* 匹配任意数量的任意字符
     */
    private val chapterPattern = Pattern.compile(
        "^.*\\s*.*(第|[Cc]hapter)\\s*[0-9零一二三四五六七八九十百千]+\\s*(章|节|卷|Section|Part).*",
    )


    /**
     * 解析 TXT 章节 (虚拟章节)
     * 策略：流式扫描全书，记录章节标题和它的字节偏移量
     */
    private fun parseSpine(context: Context, uri: Uri, bookId: Long): List<TxtChapter> {
        val chapters = mutableListOf<TxtChapter>()

        // 获取文件总大小，作为最后一章的结束点
        val totalSize = uri.getFileSize(context).getOrElse {
            logger.e { "无法获取TXT文件大小" }
            return emptyList()
        }

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            // 检测编码
            val charset = CharsetHelper.detectCharset(inputStream)

            // 重新打开流进行读取，确保从头开始
            context.contentResolver.openInputStream(uri)?.use { rawStream ->
                val bis = BufferedInputStream(rawStream)

                // 读取的字节数组
                val baos = ByteArrayOutputStream()

                // 手动字节偏移量计数
                var globalOffset = 0L

                // 标记是否为还在寻找第一章
                var isFirstChapterFound = true

                // 章节序号
                var chapterOrder = 0

                // 上一章的标题和起始位置
                var lastChapterTitle = ""
                var lastChapterStartOffset = 0L

                while (true) {
                    // 当前行之前的字节偏移
                    val currentLineStartOffset = globalOffset

                    val lineBytes = readLineBytes(bis, baos) ?: break

                    // 更新偏移量
                    globalOffset += lineBytes.size

                    // 行字符串
                    val lineStr = String(lineBytes, charset).trim()
                    if (lineStr.isBlank()) continue

                    // 如果还没找到第一章，且当前行有内容，暂存为标题
                    // 防止前面的内容没有标题
                    if (isFirstChapterFound) {
                        if (!chapterPattern.matcher(lineStr).matches()) {
                            lastChapterTitle = lineStr
                            isFirstChapterFound = false
                        }
                    }

                    // 匹配正则
                    if (chapterPattern.matcher(lineStr).matches()) {
                        // 找到新章节，结算上一章
                        // 上一章的范围：[lastChapterStartOffset, currentLineStartOffset)
                        if (currentLineStartOffset > lastChapterStartOffset && lastChapterTitle.isNotBlank()) {
                            chapters.add(
                                TxtChapter(
                                    bookId = bookId,
                                    index = chapterOrder,
                                    title = lastChapterTitle,
                                    startOffset = lastChapterStartOffset,
                                    endOffset = currentLineStartOffset,
                                    length = currentLineStartOffset - lastChapterStartOffset
                                )
                            )
                            chapterOrder++
                        }

                        // 开启新的一章
                        lastChapterTitle = lineStr
                        lastChapterStartOffset = currentLineStartOffset
                        if (isFirstChapterFound) {
                            isFirstChapterFound = false
                        }
                    }

                    baos.reset()
                }

                // 结算最后一章
                // 如果没有匹配到任何章节，也会结算唯一的一章
                if (lastChapterStartOffset < totalSize) {
                    chapters.add(
                        TxtChapter(
                            bookId = bookId,
                            index = chapterOrder,
                            title = lastChapterTitle,
                            startOffset = lastChapterStartOffset,
                            endOffset = totalSize,
                            length = totalSize - lastChapterStartOffset
                        )
                    )
                }
            }
        }

        return chapters
    }

    /**
     * 读取一行字节，包括换行符
     */
    private fun readLineBytes(bis: BufferedInputStream, baos: ByteArrayOutputStream): ByteArray? {
        var bytesReadCount = 0
        while (true) {
            val byte = bis.read()
            if (byte == -1) {
                // 流结束了
                return if (bytesReadCount > 0) baos.toByteArray()
                else null // 无数据
            }

            baos.write(byte)
            bytesReadCount++

            // 检查换行符
            if (byte == '\n'.code) {
                // Unix/Linux 换行 (\n) -> 结束
                break
            } else if (byte == '\r'.code) {
                // Mac/Windows 换行 (\r 或 \r\n)
                // 预读下一个字节看看是不是 \n
                bis.mark(1)
                val nextByte = bis.read()
                if (nextByte == '\n'.code) {
                    // 是 \r\n，把 \n 也读进来
                    baos.write(nextByte)
                } else {
                    // 只是 \r，回退那个预读的字节
                    bis.reset()
                }
                break
            }
        }
        return baos.toByteArray()
    }


    override suspend fun loadChapter(
        uriString: String,
        chapter: BookChapter
    ): List<BookContentBlock> {
        // 字节的起始位置和结束位置
        val start = (chapter as? TxtChapter)?.startOffset ?: return emptyList()
        val end = chapter.endOffset
        val byteLengthToRead = (end - start).toInt()


        if (byteLengthToRead <= 0) {
            return emptyList()
        }

        // 字符的位置
        var cursor = 0
        val bookUri = uriString.toUri()
        val contentList = mutableListOf<BookContentBlock>()

        context.contentResolver.openInputStream(bookUri)?.use { inputStream ->
            val charset = CharsetHelper.detectCharset(inputStream)

            // 需要重新打开以确保从头开始 skip
            context.contentResolver.openInputStream(bookUri)?.use { rawInputStream ->
                // 跳过前面的字节
                val skipped = rawInputStream.skipFully(start)
                if (skipped == start) {
                    // 定位成功，开始读取内容
                    // 创建字节数组，读取指定长度的内容
                    val buffer = ByteArray(byteLengthToRead)

                    // 循环读取确保读满所需的字节数
                    var totalBytesRead = 0
                    while (totalBytesRead < byteLengthToRead) {
                        val count =
                            rawInputStream.read(
                                buffer,
                                totalBytesRead,
                                byteLengthToRead - totalBytesRead
                            )
                        if (count == -1) break
                        totalBytesRead += count
                    }


                    if (totalBytesRead > 0) {
                        val rawText = String(buffer, 0, totalBytesRead, charset)

                        // 按换行符切割为段落
                        val lines = rawText.split("\n")

                        // 第一行通常是标题，做个特殊处理
                        if (lines.isNotEmpty()) {
                            val title = lines[0].trim()
                            if (title.isNotEmpty()) {
                                contentList.add(BookContentBlock.Title(title, 0, cursor))
                                cursor += title.length
                            }
                        }

                        // 其余行是段落
                        for (i in 1 until lines.size) {
                            val paragraph = lines[i].trim()
                            // 过滤掉空行，或者只包含空格的行
                            if (paragraph.isNotEmpty()) {
                                // 处理 TXT 常见的段首缩进 (全角空格)
                                val cleanText = paragraph.replace("\u3000", "  ")
                                contentList.add(
                                    BookContentBlock.Paragraph(
                                        cleanText,
                                        emptyList(),
                                        cursor
                                    )
                                )
                                cursor += cleanText.length
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