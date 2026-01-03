package io.github.lycosmic.domain.model

/**
 * 可选项
 */
data class OptionItem<T>(
    val value: T,
    val label: String,
    val selected: Boolean = false
)