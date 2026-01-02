package io.github.lycosmic.model

import kotlinx.serialization.Serializable


/**
 * 书籍目录项
 */
@Serializable
sealed class BookSpineItem {
    abstract val id: String
    abstract val order: Int // 目录项的顺序
    abstract val label: String // 目录名
}


/**
 * EPUB书籍目录项
 */
@Serializable
data class EpubSpineItem(
    override val id: String,
    override val order: Int,
    val contentHref: String, // 表示内容的 Zip 相对路径
    override val label: String // 目录名
) : BookSpineItem() {
    companion object {
        const val CONTENT_HREF_MEMBER = "contentHref"
    }
}

/**
 * TXT书籍目录项
 */
@Serializable
data class TxtSpineItem(
    override val id: String,
    override val order: Int,
    val href: String, // 目录项的链接，表示章节位置信息
    override val label: String // 目录名
) : BookSpineItem()