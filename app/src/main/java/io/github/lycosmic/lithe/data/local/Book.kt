package io.github.lycosmic.lithe.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable


@Entity(
    tableName = "books",
)
@Serializable
data class Book(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val title: String, // 书名

    val author: String = "未知", // 作者

    val description: String = "无简介", // 简介

    val fileSize: Long, // 文件大小

    val fileUri: String, // 文件真实路径 Content URI

    val format: String, // 文件格式, epub, pdf, txt

    val coverUri: String? = null, // 封面缓存路径

    val lastReadPosition: String? = null, // 最后阅读位置

    val progress: Float = 0f, // 阅读进度百分比

    val importTime: Long = System.currentTimeMillis(), // 导入时间, 用于最近添加排序

    val lastReadTime: Long = System.currentTimeMillis(), // 最后阅读时间, 用于最近阅读排序
)