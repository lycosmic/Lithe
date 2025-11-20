package io.github.lycosmic.lithe.data.model

import androidx.compose.runtime.Immutable

@Immutable // 优化重组检查
data class OptionItem<T>(
    val value: T,
    val label: String,
    val selected: Boolean = false
)