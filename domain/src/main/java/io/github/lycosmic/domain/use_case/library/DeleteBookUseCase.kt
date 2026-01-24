package io.github.lycosmic.domain.use_case.library

import io.github.lycosmic.domain.repository.BookRepository
import io.github.lycosmic.domain.repository.ChapterRepository
import javax.inject.Inject

/**
 * 从书库中删除书籍
 */
class DeleteBookUseCase @Inject constructor(
    private val bookRepository: BookRepository,
    private val chapterRepository: ChapterRepository
) {
    suspend operator fun invoke(bookId: Long): Result<Unit> = runCatching {
        // 1. 删除书籍对应的章节
        chapterRepository.deleteChaptersByBookId(bookId)
        // 2. 删除书籍
        bookRepository.deleteBook(bookId)
    }
}