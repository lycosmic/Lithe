package io.github.lycosmic.domain.repository


import io.github.lycosmic.domain.model.Book
import io.github.lycosmic.domain.model.Category
import io.github.lycosmic.domain.model.CategoryWithBookList
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
    fun getBookFlowById(bookId: Long): Flow<Result<Book>>

    /**
     * 根据ID获取书籍
     */
    suspend fun getBookById(bookId: Long): Result<Book>

    /**
     * 根据标识符获取书籍
     */
    suspend fun getBookByUri(uri: String): Book?

    /**
     * 获取书籍的分类
     */
    fun getBookCategoriesFlow(bookId: Long): Flow<List<Category>>

    /**
     * 获取所有分类和书籍关系
     */
    fun getCategoryWithBooksFlow(): Flow<List<CategoryWithBookList>>

    /**
     * 移动一些书籍到某些分类中
     */
    suspend fun moveBooksToCategories(bookIds: List<Long>, categoryIds: List<Long>)
}