package io.github.lycosmic.model

/**
 * 可选项
 */
data class OptionItem<T>(
    val value: T,
    val label: String,
    val selected: Boolean = false
)