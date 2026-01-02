package io.github.lycosmic.lithe.data.repository

import io.github.lycosmic.lithe.data.local.dao.BookDao
import io.github.lycosmic.lithe.data.mapper.toDomain
import io.github.lycosmic.lithe.data.mapper.toEntity
import io.github.lycosmic.model.Book
import io.github.lycosmic.model.CategoryWithBookList
import io.github.lycosmic.repository.BookRepository
import kotlinx.coroutines.flow.Flow
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

    override suspend fun deleteBook(bookId: Long) {
        bookDao.deleteBookById(id = bookId)
    }

    override fun getBookFlowById(bookId: Long): Flow<Book?> {
        return bookDao.getBookFlowById(id = bookId).map { it?.toDomain() }
    }

    override suspend fun getBookById(bookId: Long): Result<Book> {
        return runCatching {
            val entity = bookDao.getBookById(id = bookId)
                ?: throw Exception("找不到 ID 为 $bookId 的书籍")

            entity.toDomain()
        }
    }

    override suspend fun getBookByUniqueId(uniqueId: String): Book? {
        return bookDao.getBookByUniqueId(uniqueId = uniqueId)?.toDomain()
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