package io.github.lycosmic.domain.model

/**
 * 书籍分类
 */
data class Category(
    val id: Long = 0L,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val DEFAULT_CATEGORY_ID = 1L
    }
}