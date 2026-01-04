package io.github.lycosmic.domain.use_case.browse

import io.github.lycosmic.domain.model.Book
import io.github.lycosmic.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetBookUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    operator fun invoke(bookId: Long): Flow<Result<Book>> {
        return bookRepository.getBookFlowById(bookId)
    }
}