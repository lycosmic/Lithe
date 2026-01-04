package io.github.lycosmic.lithe.presentation.reader

import io.github.lycosmic.domain.model.Book
import io.github.lycosmic.domain.model.BookChapter
import io.github.lycosmic.domain.model.ReadingProgress
import io.github.lycosmic.lithe.presentation.reader.model.ReaderContent

data class ReaderUiState(
    val isLoading: Boolean = true,
    val book: Book? = null,
    val chapters: List<BookChapter> = emptyList(), // 目录结构
    val currentContent: List<ReaderContent> = emptyList(), // 当前章的内容
    val currentProgress: ReadingProgress? = null, // 阅读进度
    val error: String? = null, // 错误信息
)