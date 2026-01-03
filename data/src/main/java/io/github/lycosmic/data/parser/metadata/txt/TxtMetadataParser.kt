package io.github.lycosmic.data.parser.metadata.txt

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.lycosmic.data.parser.metadata.MetadataParserStrategy
import io.github.lycosmic.data.util.getFileName
import io.github.lycosmic.model.BookMetadata
import java.util.regex.Pattern
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TxtMetadataParser @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : MetadataParserStrategy {

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override suspend fun parse(
        uriString: String
    ): BookMetadata {
        val uri = uriString.toUri()

        val txtMetadata = parseTxtMetadata(context, uri)
        return BookMetadata(
            uniqueId = uri.toString(),
            title = txtMetadata.title,
            authors = txtMetadata.authors,
            description = txtMetadata.description,
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


    // 匹配作者的正则
    // ^\s* 匹配行首可能有的空格
    private val authorPattern =
        Pattern.compile("^\\s*(?:作者|Author)[:：]\\s*(.*)", Pattern.CASE_INSENSITIVE)

    // 简介正则
    private val descriptionPattern =
        Pattern.compile("^\\s*(?:简介|Description)[:：]\\s*(.*)", Pattern.CASE_INSENSITIVE)

    /**
     * 解析 TXT 文件的元数据
     */
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private fun parseTxtMetadata(context: Context, uri: Uri): TxtMetadata {
        // 使用文件名作为标题
        val txtFileName = uri.getFileName(context).getOrElse {
            throw it
        }
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

}