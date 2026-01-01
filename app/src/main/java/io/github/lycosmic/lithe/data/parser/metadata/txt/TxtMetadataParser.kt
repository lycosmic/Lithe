package io.github.lycosmic.lithe.data.parser.metadata.txt

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.lycosmic.lithe.data.parser.metadata.BookMetadataParser
import io.github.lycosmic.lithe.domain.model.ParsedMetadata
import io.github.lycosmic.lithe.domain.model.TxtSpineItem
import io.github.lycosmic.lithe.utils.CharsetUtils
import io.github.lycosmic.lithe.utils.FileUtils
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TxtMetadataParser @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : BookMetadataParser {
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override suspend fun parse(
        uri: Uri
    ): ParsedMetadata {
        val txtMetadata = parseTxtMetadata(context, uri)
        val spineList = parseSpine(context, uri)
        return ParsedMetadata(
            uniqueId = uri.toString(),
            title = txtMetadata.title,
            authors = txtMetadata.authors,
            description = txtMetadata.description,
            bookmarks = spineList
        )
    }

    /**
     * TXT 文本文件的作者、标题、简介
     */
    private data class TxtMetadata(
        val title: String? = null,
        val authors: List<String>? = null,
        val description: String? = null
    )

    /**
     * 解析 TXT 文件的元数据
     */
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private fun parseTxtMetadata(context: Context, uri: Uri): TxtMetadata {
        // 使用文件名作为标题
        val txtFileName = FileUtils.getFileName(context, uri)
        val title = txtFileName.substringBeforeLast(".")

        // 作者扫描书籍内容
        val authors: MutableList<String> = mutableListOf()

        // 简介扫描书籍内容
        var description: String? = null

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val bufferedReader = inputStream.bufferedReader(Charsets.UTF_8)

            // 只扫描前100行
            var lineCount = 0
            var line: String
            while (bufferedReader.readLine().also { line = it } != null && lineCount < 100) {
                val content = line.trim()
                if (content.isBlank()) {
                    lineCount++
                    continue
                }

                val authorMatcher = authorPattern.matcher(content)
                if (authorMatcher.find()) {
                    val author = authorMatcher.group(1)?.trim() ?: ""
                    authors.addLast(author)
                }

                val descriptionMatcher = descriptionPattern.matcher(content)
                if (descriptionMatcher.find()) {
                    description = descriptionMatcher.group(1)?.trim() ?: ""
                }

                lineCount++
            }
        }

        return TxtMetadata(
            title = title,
            authors = authors,
            description = description
        )
    }

    // 匹配作者的正则
    // ^\s* 匹配行首可能有的空格
    private val authorPattern =
        Pattern.compile("^\\s*(?:作者|Author)[:：]\\s*(.*)", Pattern.CASE_INSENSITIVE)

    // 简介正则
    private val descriptionPattern =
        Pattern.compile("^\\s*(?:简介|Description)[:：]\\s*(.*)", Pattern.CASE_INSENSITIVE)

    // 章节正则
    private val chapterPattern = Pattern.compile(
        "^\\s*(?:第|Chapter)\\s*[0-9零一二三四五六七八九十百千]+\\s*(?:章|节|卷|Section|Part).*",
        Pattern.CASE_INSENSITIVE
    )

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