package io.github.lycosmic.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import io.github.lycosmic.data.local.coverter.Converters
import kotlinx.serialization.Serializable

@Entity(
    tableName = BookEntity.TABLE_NAME,
)
@TypeConverters(Converters::class)
@Serializable
data class BookEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COL_ID)
    val id: Long = 0,
    @ColumnInfo(name = COL_TITLE)
    val title: String, // 书名
    @ColumnInfo(name = COL_AUTHOR)
    val author: String = "未知", // 作者
    @ColumnInfo(name = COL_DESCRIPTION)
    val description: String?, // 简介
    @ColumnInfo(name = COL_LANGUAGE)
    val language: String?,
    @ColumnInfo(name = COL_PUBLISHER)
    val publisher: String?,
    @ColumnInfo(name = COL_SUBJECTS)
    val subjects: List<String>?,
    @ColumnInfo(name = COL_FILE_SIZE)
    val fileSize: Long, // 文件大小
    @ColumnInfo(name = COL_FILE_URI)
    val fileUri: String, // 文件真实路径 Content URI
    @ColumnInfo(name = COL_FORMAT)
    val format: String, // 文件格式, epub, pdf, txt
    @ColumnInfo(name = COL_COVER_PATH)
    val coverPath: String? = null, // 封面缓存路径
    @ColumnInfo(name = COL_IMPORT_TIME)
    val importTime: Long = System.currentTimeMillis(), // 导入时间, 用于最近添加排序
) {
    companion object {
        const val TABLE_NAME = "books"
        const val COL_ID = "id"
        const val COL_TITLE = "title"
        const val COL_AUTHOR = "author"
        const val COL_DESCRIPTION = "description"
        const val COL_LANGUAGE = "language"
        const val COL_PUBLISHER = "publisher"
        const val COL_SUBJECTS = "subjects"
        const val COL_FILE_SIZE = "file_size"
        const val COL_FILE_URI = "file_uri"
        const val COL_FORMAT = "format"
        const val COL_COVER_PATH = "cover_path"
        const val COL_IMPORT_TIME = "import_time"
    }
}