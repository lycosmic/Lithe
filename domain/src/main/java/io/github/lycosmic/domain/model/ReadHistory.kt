package io.github.lycosmic.domain.model


/**
 * 阅读历史
 */
data class ReadHistory(
    val id: Int = 0,
    val book: Book,
    val time: Long
)