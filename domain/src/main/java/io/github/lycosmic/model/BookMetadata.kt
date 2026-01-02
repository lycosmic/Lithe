package io.github.lycosmic.model

/**
 * 解析的书籍元数据
 */
data class BookMetadata(
    val uniqueId: String? = null,
    val title: String? = null,
    val authors: List<String>? = null,
    val language: String? = null,
    val description: String? = null,
    val publisher: String? = null,
    val subjects: List<String>? = null,
    val coverPath: String? = null, // 本地缓存后的封面路径
)