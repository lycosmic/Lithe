package io.github.lycosmic.lithe.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * 用户授权的文件夹
 */
@Entity(
    tableName = "scanned_directories",
)
@Serializable
data class ScannedDirectory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uri: String, // 文件夹 URI
    val root: String, // 内部共享存储空间根目录
    val path: String, // 文件夹相对路径
    val addedTime: Long = System.currentTimeMillis(),
)