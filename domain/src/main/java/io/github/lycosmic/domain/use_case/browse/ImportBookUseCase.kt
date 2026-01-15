package io.github.lycosmic.domain.use_case.browse

import io.github.lycosmic.domain.model.Book
import io.github.lycosmic.domain.model.Category
import io.github.lycosmic.domain.model.ParsedBook
import io.github.lycosmic.domain.repository.BookRepository
import io.github.lycosmic.domain.util.DomainLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * 导入书籍
 */
class ImportBookUseCase @Inject constructor(
    private val bookRepository: BookRepository,
    private val logger: DomainLogger
) {
    suspend operator fun invoke(parsedBooks: List<ParsedBook>): Result<Long> =
        withContext(Dispatchers.IO) {
            logger.d { "开始导入用户选择的书籍，共 ${parsedBooks.size} 本书" }

            var successCount = 0L

            try {
                parsedBooks.forEach { parsedBook ->
                    val metadata = parsedBook.metadata
                    val bookFile = parsedBook.file
                    val uniqueId = metadata.uniqueId ?: bookFile.uriString

                    if (bookRepository.getBookByUniqueId(uniqueId) != null) {
                        // 已有该书籍
                        logger.w {
                            "书库中已有该书籍: ${metadata.title}，唯一标符为 $uniqueId，忽略该文件: ${bookFile.name}"
                        }
                        return@forEach
                    }

                    // 导入数据库
                    val book = Book(
                        id = 0,
                        uniqueId = uniqueId,
                        title = metadata.title ?: "Unknown Title",
                        author = metadata.authors ?: emptyList(),
                        description = metadata.description,
                        fileSize = bookFile.size,
                        fileUri = bookFile.uriString,
                        format = bookFile.format,
                        coverPath = metadata.coverPath,
                        importTime = System.currentTimeMillis(),
                        progress = 0f,
                    )
                    val bookId = bookRepository.importBook(book)
                    // 添加到默认分类中
                    bookRepository.moveBooksToCategories(
                        listOf(bookId),
                        listOf(Category.DEFAULT_CATEGORY_ID)
                    )
                    successCount++
                }
            } catch (e: Exception) {
                return@withContext Result.failure(e)
            }

            logger.d { "导入用户选择的书籍完成，成功导入 $successCount 本书" }

            return@withContext Result.success(successCount)
        }
}