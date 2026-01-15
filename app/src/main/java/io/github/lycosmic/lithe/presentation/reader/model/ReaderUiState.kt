package io.github.lycosmic.lithe.presentation.reader.model

import io.github.lycosmic.domain.model.BookChapter
import io.github.lycosmic.domain.model.ReadingProgress

data class ReaderUiState(
    val bookId: Long = -1,
    val bookName: String = "",
    val currentChapterIndex: Int = -1,
    val chapters: List<BookChapter> = emptyList(), // 目录结构
    val readerItems: List<ReaderContent> = emptyList(), // 阅读内容
    val progress: ReadingProgress = ReadingProgress.default(-1L), // 阅读进度
    val isInitialLoading: Boolean = true, // 全屏加载
    val isAppendLoading: Boolean = false, // 追加加载
    val readerSettings: ReaderSettings = ReaderSettings(), // 阅读设置
    val error: String? = null, // 错误信息
    val currentContent: ReaderContent? = null
)


/**
 * 阅读器设置
 */
data class ReaderSettings(
    val textSizeSp: Float = 18f,
    val lineHeightMultiplier: Float = 1.5f,
    val backgroundColor: Int = 0xFFFFFFFF.toInt(),
    val textColor: Int = 0xFF000000.toInt()
)

