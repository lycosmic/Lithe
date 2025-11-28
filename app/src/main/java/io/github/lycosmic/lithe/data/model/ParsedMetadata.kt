package io.github.lycosmic.lithe.data.model

/**
 * 书籍解析结果
 */
data class ParsedMetadata(
    val uniqueId: String? = null,
    val title: String? = null,
    val authors: List<String>? = null,
    val language: String? = null,
    val description: String? = null,
    val publisher: String? = null,
    val subjects: List<String>? = null,
    val coverPath: String? = null, // 本地缓存后的封面路径
    val bookmarks: List<BookSpineItem>? = null // 书签列表
)