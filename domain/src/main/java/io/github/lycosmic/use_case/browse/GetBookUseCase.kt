package io.github.lycosmic.use_case.browse

import io.github.lycosmic.model.Book
import io.github.lycosmic.repository.BookRepository
import javax.inject.Inject


class GetBookUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(bookId: Long): Result<Book> {
        return bookRepository.getBookById(bookId)
    }
}