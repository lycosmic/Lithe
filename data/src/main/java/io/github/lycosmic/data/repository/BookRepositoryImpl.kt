package io.github.lycosmic.data.repository

import io.github.lycosmic.data.local.dao.BookDao
import io.github.lycosmic.data.mapper.toDomain
import io.github.lycosmic.data.mapper.toEntity
import io.github.lycosmic.domain.model.Book
import io.github.lycosmic.domain.model.Category
import io.github.lycosmic.domain.model.CategoryWithBookList
import io.github.lycosmic.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class BookRepositoryImpl @Inject constructor(
    private val bookDao: BookDao
) : BookRepository {

    override fun getAllBooks(): Flow<List<Book>> {
        return bookDao.getAllBooks().map {
            it.map { book ->
                book.toDomain()
            }
        }
    }

    override suspend fun importBook(book: Book): Long {
        val bookId = bookDao.insertBook(bookEntity = book.toEntity())
        return bookId
    }

    /**
     * 更新书籍的编码
     */
    override suspend fun updateBookCharset(
        bookId: Long,
        charset: String
    ) {
        val bookEntity = bookDao.getBookById(id = bookId) ?: return
        bookDao.updateBook(bookEntity.copy(charsetName = charset))
    }

    override suspend fun deleteBook(bookId: Long) {
        bookDao.deleteBookById(id = bookId)
    }

    override fun getBookFlowById(bookId: Long): Flow<Result<Book>> {
        return bookDao.getBookFlowById(id = bookId).map {
            if (it == null) {
                return@map Result.failure(Exception("找不到 ID 为 $bookId 的书籍"))
            }
            Result.success(it.toDomain())
        }.catch { e ->
            emit(Result.failure(e))
        }
    }

    override suspend fun getBookById(bookId: Long): Result<Book> {
        return runCatching {
            val entity = bookDao.getBookById(id = bookId)
                ?: throw Exception("找不到 ID 为 $bookId 的书籍")

            entity.toDomain()
        }
    }

    override suspend fun getBookByUri(uri: String): Book? {
        return bookDao.getBookByUri(uri)?.toDomain()
    }

    override fun getBookCategoriesFlow(bookId: Long): Flow<List<Category>> {
        return bookDao.getBookCategoriesFlow(bookId = bookId)
    }

    override fun getCategoryWithBooksFlow(): Flow<List<CategoryWithBookList>> {
        return bookDao.getCategoriesWithBooks().map {
            it.map { categoryWithBooks ->
                CategoryWithBookList(
                    category = categoryWithBooks.category.toDomain(),
                    bookList = categoryWithBooks.bookEntities.map { bookEntity ->
                        bookEntity.toDomain()
                    }
                )

            }
        }
    }

    override suspend fun moveBooksToCategories(bookIds: List<Long>, categoryIds: List<Long>) {
        bookIds.forEach {
            bookDao.moveBookToCategories(bookId = it, categoryIds = categoryIds)
        }
    }

}