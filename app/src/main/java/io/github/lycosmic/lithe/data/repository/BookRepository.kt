package io.github.lycosmic.lithe.data.repository

import io.github.lycosmic.lithe.data.local.dao.BookDao
import io.github.lycosmic.lithe.data.local.entity.Book
import io.github.lycosmic.lithe.data.local.entity.BookCategoryCrossRef
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class BookRepository @Inject constructor(
    private val bookDao: BookDao
) {

    fun getAllBooks() = bookDao.getAllBooks()

    suspend fun importBook(book: Book): Long {
        return bookDao.insertBook(book = book)
    }

    /**
     * 删除书籍
     */
    suspend fun deleteBook(bookId: Long) {
        bookDao.deleteBookById(id = bookId)
    }

    /**
     * 根据ID获取书籍
     * 以流的形式
     */
    fun getBookFlow(bookId: Long) = bookDao.getBookFlowById(id = bookId)

    /**
     * 根据ID获取书籍
     * 以普通方式
     */
    suspend fun getBookById(bookId: Long) = bookDao.getBookById(id = bookId)

    /**
     * 根据标识符获取书籍
     */
    suspend fun getBookByUniqueId(uniqueId: String) = bookDao.getBookByUniqueId(uniqueId = uniqueId)

    /**
     * 获取所有书籍和分类关系
     */
    fun getBooksWithCategories() = bookDao.getBooksWithCategories()

    /**
     * 获取所有分类和书籍关系
     */
    fun getCategoryWithBooks() = bookDao.getCategoriesWithBooks()

    /**
     * 插入书籍分类关联
     */
    suspend fun insertBookCategoryCrossRefs(refs: List<BookCategoryCrossRef>) {
        bookDao.insertBookCategoryCrossRefs(refs = refs)
    }

    /**
     * 移动书籍到
     */
    suspend fun moveBooksToCategories(bookIds: List<Long>, categoryIds: List<Long>) {
        bookIds.forEach {
            bookDao.moveBookToCategories(bookId = it, categoryIds = categoryIds)
        }
    }

}