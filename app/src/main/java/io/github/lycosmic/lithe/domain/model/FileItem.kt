package io.github.lycosmic.lithe.domain.model

import android.net.Uri
import androidx.compose.runtime.Immutable


@Immutable
data class FileItem(
    val name: String, // 文件名
    val uri: Uri,
    val type: FileFormat,
    val size: Long,
    val parentPath: String, // 父文件夹的完整路径
    val lastModified: Long,
)