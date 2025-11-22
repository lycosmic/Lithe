package io.github.lycosmic.lithe.data.model

/**
 * 文件解析结果
 */
data class ParsedMetadata(
    val title: String,
    val author: String,
    val description: String? = null,
    val coverPath: String? = null // 本地缓存后的封面路径
)