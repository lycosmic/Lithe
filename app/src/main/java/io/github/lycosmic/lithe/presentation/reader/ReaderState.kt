package io.github.lycosmic.lithe.presentation.reader

import io.github.lycosmic.domain.model.Book
import io.github.lycosmic.domain.model.BookChapter
import io.github.lycosmic.domain.model.ReadingProgress

data class ReaderState(
    val book: Book = Book.default,
    val currentChapterIndex: Int = 0,
    val chapters: List<BookChapter> = emptyList(), // 目录结构
    val readerItems: List<ReaderContent> = emptyList(), // 阅读内容
    val progress: ReadingProgress = ReadingProgress.default(-1L), // 阅读进度
    val chapterProgress: Float = 0f, // 章节进度
    val isInitialLoading: Boolean = true, // 全屏加载
    val isAppendLoading: Boolean = false, // 追加加载
    val error: String? = null, // 错误信息
    val currentChapterLength: Int = 1 // 当前章节的字符长度，防止除以0，默认为1
)
