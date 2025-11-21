package io.github.lycosmic.lithe.data.model

import android.net.Uri
import androidx.compose.runtime.Immutable


@Immutable
data class SelectableFileItem(
    val name: String, // 文件名
    val uri: Uri,
    val type: FileType,
    val size: Long,
    val selected: Boolean, // 是否被选中
    val parentPath: String, // 父文件夹的完整路径
    val lastModified: Long,
)