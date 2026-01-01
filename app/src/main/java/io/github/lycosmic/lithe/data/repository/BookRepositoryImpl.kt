package io.github.lycosmic.lithe.data.repository

import io.github.lycosmic.lithe.data.local.dao.BookDao
import io.github.lycosmic.lithe.data.local.entity.BookCategoryCrossRef
import io.github.lycosmic.lithe.data.local.entity.BookEntity
import io.github.lycosmic.lithe.domain.model.BookWithCategories
import io.github.lycosmic.lithe.domain.model.CategoryWithBooks
import io.github.lycosmic.lithe.domain.repository.BookRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class BookRepositoryImpl @Inject constructor(
    private val bookDao: BookDao
) : BookRepository {

    override fun getAllBooks() = bookDao.getAllBooks()

    override suspend fun importBook(bookEntity: BookEntity): Long {
        return bookDao.insertBook(bookEntity = bookEntity)
    }

    override suspend fun deleteBook(bookId: Long) {
        bookDao.deleteBookById(id = bookId)
    }

    override fun getBookFlowById(bookId: Long): Flow<BookEntity?> {
        return bookDao.getBookFlowById(id = bookId)
    }

    override suspend fun getBookById(bookId: Long) = bookDao.getBookById(id = bookId)

    override suspend fun getBookByUniqueId(uniqueId: String) =
        bookDao.getBookByUniqueId(uniqueId = uniqueId)

    override fun getBooksWithCategoriesFlow(): Flow<List<BookWithCategories>> {
        return bookDao.getBooksWithCategories()
    }

    override fun getCategoryWithBooksFlow(): Flow<List<CategoryWithBooks>> {
        return bookDao.getCategoriesWithBooks()
    }

    override suspend fun insertBookCategoryCrossRefs(refs: List<BookCategoryCrossRef>) {
        bookDao.insertBookCategoryCrossRefs(refs = refs)
    }

    override suspend fun moveBooksToCategories(bookIds: List<Long>, categoryIds: List<Long>) {
        bookIds.forEach {
            bookDao.moveBookToCategories(bookId = it, categoryIds = categoryIds)
        }
    }

}