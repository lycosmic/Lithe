package io.github.lycosmic.lithe.data.model.content

/**
 * 书籍目录顺序
 */
data class BookSpineItem(
    val id: String,
    val order: Int,
    val contentHref: String, // 内容的 Zip 相对路径
    val label: String? = null // 标题
)