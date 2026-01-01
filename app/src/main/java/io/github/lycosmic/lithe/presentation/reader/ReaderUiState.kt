package io.github.lycosmic.lithe.presentation.reader

import io.github.lycosmic.lithe.data.local.entity.BookEntity
import io.github.lycosmic.lithe.domain.model.BookSpineItem
import io.github.lycosmic.lithe.domain.model.ReaderContent

data class ReaderUiState(
    val isLoading: Boolean = true,
    val bookEntity: BookEntity? = null,
    val spine: List<BookSpineItem> = emptyList(), // 目录结构
    val currentChapterIndex: Int = 0,             // 当前第几章
    val currentContent: List<ReaderContent> = emptyList(), // 当前章的内容
)