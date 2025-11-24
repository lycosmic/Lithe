package io.github.lycosmic.lithe.data.model.content

/**
 * 书籍的脊柱
 *
 */
data class BookSpineItem(
    val id: String,
    val href: String, // 文件在 zip 包的相对路径
    val title: String? = null // 章节名
)