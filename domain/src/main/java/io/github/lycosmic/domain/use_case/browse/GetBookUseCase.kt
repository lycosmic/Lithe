package io.github.lycosmic.domain.use_case.browse

import io.github.lycosmic.domain.model.Book
import io.github.lycosmic.domain.repository.BookRepository
import javax.inject.Inject

/**
 * 获取书籍信息
 */
class GetBookUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(bookId: Long): Result<Book> {
        return bookRepository.getBookById(bookId)
    }
}