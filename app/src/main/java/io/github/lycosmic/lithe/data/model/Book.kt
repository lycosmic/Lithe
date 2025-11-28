package io.github.lycosmic.lithe.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import io.github.lycosmic.lithe.data.local.converter.EpubTypeConverters
import io.github.lycosmic.lithe.data.model.content.BookSpineItem
import kotlinx.serialization.Serializable

@Entity(
    tableName = "books",
)
@TypeConverters(EpubTypeConverters::class)
@Serializable
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String, // 书名

    val uniqueId: String, // 书籍唯一标识符

    val author: String = "未知", // 作者

    val description: String?, // 简介

    val language: String?,

    val publisher: String?,

    val subjects: List<String>?,

    val fileSize: Long, // 文件大小

    val fileUri: String, // 文件真实路径 Content URI

    val format: String, // 文件格式, epub, pdf, txt

    val coverPath: String? = null, // 封面缓存路径

    val lastReadPosition: String? = null, // 最后阅读位置

    val progress: Float = 0f, // 阅读进度百分比

    val bookmarks: List<BookSpineItem>? = null, // 书签

    val importTime: Long = System.currentTimeMillis(), // 导入时间, 用于最近添加排序

    val lastReadTime: Long?, // 最后阅读时间, 用于最近阅读排序
) {
    companion object {
        val defaultBook = Book(
            id = -1,
            title = "默认书籍",
            author = "未知",
            description = "默认书籍",
            fileSize = 0,
            fileUri = "",
            format = "txt",
            coverPath = null,
            lastReadPosition = null,
            progress = 0f,
            lastReadTime = null,
            uniqueId = "default",
            language = "zh-CN",
            publisher = "未知",
            subjects = emptyList(),
        )
    }
}