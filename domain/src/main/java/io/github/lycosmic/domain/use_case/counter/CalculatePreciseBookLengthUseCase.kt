package io.github.lycosmic.domain.use_case.counter

import io.github.lycosmic.domain.length.BookLengthCalculator
import io.github.lycosmic.domain.model.Book
import io.github.lycosmic.domain.model.BookChapter
import io.github.lycosmic.domain.model.FileFormat
import io.github.lycosmic.domain.repository.BookRepository
import io.github.lycosmic.domain.repository.ChapterRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CalculatePreciseBookLengthUseCase @Inject constructor(
    private val bookRepository: BookRepository,
    private val chapterRepository: ChapterRepository,
    private val lengthCalculator: BookLengthCalculator
) {
    suspend operator fun invoke(book: Book, currentChapters: List<BookChapter>): List<BookChapter> {
        return withContext(Dispatchers.IO) {

            // 1. 进行计算
            val result = lengthCalculator.calculate(book, currentChapters)

            // 2. 如果在这个过程中检测到了新编码，保存到 Book 表
            if (book.format == FileFormat.TXT &&
                book.charset == null &&
                result.charset != null
            ) {
                bookRepository.updateBookCharset(book.id, result.charset)
            }

            // 3. 保存章节长度
            chapterRepository.saveChapters(result.chapters)

            // 返回新列表
            result.chapters
        }
    }
}