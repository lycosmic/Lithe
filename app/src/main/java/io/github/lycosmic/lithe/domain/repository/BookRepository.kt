package io.github.lycosmic.lithe.domain.repository

import io.github.lycosmic.lithe.data.local.entity.Book
import io.github.lycosmic.lithe.data.local.entity.BookCategoryCrossRef
import io.github.lycosmic.lithe.data.model.BookWithCategories
import io.github.lycosmic.lithe.data.model.CategoryWithBooks
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    /**
     * 获取所有书籍
     */
    fun getAllBooks(): Flow<List<Book>>

    /**
     * 插入书籍
     */
    suspend fun importBook(book: Book): Long

    /**
     * 删除书籍
     */
    suspend fun deleteBook(bookId: Long)

    /**
     * 根据ID获取书籍
     */
    fun getBookFlowById(bookId: Long): Flow<Book?>

    /**
     * 根据ID获取书籍
     */
    suspend fun getBookById(bookId: Long): Book?

    /**
     * 根据标识符获取书籍
     */
    suspend fun getBookByUniqueId(uniqueId: String): Book?

    /**
     * 获取所有书籍和分类关系
     */
    fun getBooksWithCategoriesFlow(): Flow<List<BookWithCategories>>

    /**
     * 获取所有分类和书籍关系
     */
    fun getCategoryWithBooksFlow(): Flow<List<CategoryWithBooks>>

    /**
     * 插入书籍分类关联
     */
    suspend fun insertBookCategoryCrossRefs(refs: List<BookCategoryCrossRef>)

    /**
     * 移动书籍到多个分类中
     */
    suspend fun moveBooksToCategories(bookIds: List<Long>, categoryIds: List<Long>)
}