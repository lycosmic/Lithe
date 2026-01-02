package io.github.lycosmic.lithe.presentation.reader

import io.github.lycosmic.lithe.presentation.reader.model.ReaderContent
import io.github.lycosmic.model.Book
import io.github.lycosmic.model.BookSpineItem

data class ReaderUiState(
    val isLoading: Boolean = true,
    val book: Book? = null,
    val chapters: List<BookSpineItem> = emptyList(), // 目录结构
    val currentChapterIndex: Int = 0,             // 当前第几章
    val currentContent: List<ReaderContent> = emptyList(), // 当前章的内容
)