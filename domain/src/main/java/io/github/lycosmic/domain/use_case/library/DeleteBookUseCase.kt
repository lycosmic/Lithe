package io.github.lycosmic.domain.use_case.library

import io.github.lycosmic.domain.repository.BookRepository
import io.github.lycosmic.domain.repository.ChapterRepository
import io.github.lycosmic.domain.repository.ProgressRepository
import io.github.lycosmic.domain.repository.ReadHistoryRepository
import javax.inject.Inject

/**
 * 从书库中删除书籍
 */
class DeleteBookUseCase @Inject constructor(
    private val bookRepository: BookRepository,
    private val chapterRepository: ChapterRepository,
    private val progressRepository: ProgressRepository,
    private val historyRepository: ReadHistoryRepository
) {
    suspend operator fun invoke(bookId: Long): Result<Unit> = runCatching {
        // 1. 删除书籍对应的章节
        chapterRepository.deleteChaptersByBookId(bookId)
        // 2. 删除书籍对应的阅读进度
        progressRepository.deleteProgress(bookId)
        // 3. 删除书籍对应的阅读历史
        historyRepository.deleteHistoryByBookId(bookId)
        // 4. 删除书籍
        bookRepository.deleteBook(bookId)
    }
}