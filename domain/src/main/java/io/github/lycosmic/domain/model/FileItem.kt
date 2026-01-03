package io.github.lycosmic.domain.model

data class FileItem(
    val name: String, // 文件名
    val uriString: String,
    val format: FileFormat,
    val size: Long,
    val parentPath: String, // 父文件夹的完整路径
    val lastModified: Long,
)