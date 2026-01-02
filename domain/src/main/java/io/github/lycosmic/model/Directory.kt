package io.github.lycosmic.model

/**
 * 文件夹信息
 */
data class Directory(
    val id: Long = 0,
    val uriString: String,
    val root: String,
    val path: String,
    val addTime: Long
)