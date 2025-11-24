package io.github.lycosmic.lithe.data.parser.content

import android.content.Context
import android.net.Uri
import io.github.lycosmic.lithe.data.model.content.BookSpineItem
import io.github.lycosmic.lithe.data.model.content.ReaderContent

interface BookContentParser {
    /**
     * 解析整本书的结构
     * 返回按照阅读顺序排序的文件列表
     */
    suspend fun parseSpine(context: Context, uri: Uri): List<BookSpineItem>

    /**
     * 解析当前章节的内容
     */
    suspend fun parseContent(context: Context, bookUri: Uri, href: String): List<ReaderContent>
}