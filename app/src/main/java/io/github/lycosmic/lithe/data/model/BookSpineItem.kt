package io.github.lycosmic.lithe.data.model

import kotlinx.serialization.Serializable

/**
 * 书籍目录顺序
 */
@Serializable
data class BookSpineItem(
    val id: String,
    val order: Int,
    val contentHref: String, // 内容的 Zip 相对路径
    val label: String // 标题
)