package io.github.lycosmic.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * 用户授权的文件夹
 */
@Entity(
    tableName = AuthorizedDirectory.TABLE_NAME,
)
@Serializable
data class AuthorizedDirectory(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COL_ID)
    val id: Long = 0,
    @ColumnInfo(name = COL_URI)
    val uri: String, // 文件夹 URI
    @ColumnInfo(name = COL_ROOT)
    val root: String, // 内部共享存储空间根目录
    @ColumnInfo(name = COL_PATH)
    val path: String, // 文件夹相对路径
    @ColumnInfo(name = COL_ADD_TIME)
    val addTime: Long = System.currentTimeMillis(),
) {
    companion object {
        const val TABLE_NAME = "authorized_directories"
        const val COL_ID = "id"
        const val COL_URI = "uri"
        const val COL_ROOT = "root"
        const val COL_PATH = "path"
        const val COL_ADD_TIME = "add_time"
    }
}