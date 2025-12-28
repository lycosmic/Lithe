package io.github.lycosmic.lithe.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import io.github.lycosmic.lithe.data.local.entity.Book
import io.github.lycosmic.lithe.data.local.entity.BookCategoryCrossRef
import io.github.lycosmic.lithe.data.model.BookWithCategories
import io.github.lycosmic.lithe.data.model.CategoryWithBooks
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Query("SELECT * FROM books ORDER BY lastReadTime DESC")
    fun getAllBooks(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE id = :id LIMIT 1")
    suspend fun getBookById(id: Long): Book?

    @Query("SELECT * FROM books WHERE id = :id LIMIT 1")
    fun getBookFlowById(id: Long): Flow<Book?>

    /**
     * 插入书籍，返回新书的 ID
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBook(book: Book): Long

    @Delete
    suspend fun deleteBook(book: Book)

    @Query("DELETE FROM books WHERE id=:id")
    suspend fun deleteBookById(id: Long)

    @Query("SELECT * FROM books WHERE uniqueId = :uniqueId LIMIT 1")
    suspend fun getBookByUniqueId(uniqueId: String): Book?

    /**
     * 插入书籍分类关联
     */
    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    suspend fun insertBookCategoryCrossRefs(refs: List<BookCategoryCrossRef>)

    /**
     * 删除某本书的所有分类关联，用于更新分类时，先清空再重新插入
     */
    @Query("DELETE FROM book_category_cross_ref WHERE book_id = :bookId")
    suspend fun deleteCrossRefsByBookId(bookId: Long)


    /**
     * 移动单本书籍到指定分类
     */
    @Transaction
    suspend fun moveBookToCategories(bookId: Long, categoryIds: List<Long>) {
        deleteCrossRefsByBookId(bookId)

        val newRefs = categoryIds.map { categoryId ->
            BookCategoryCrossRef(bookId = bookId, categoryId = categoryId)
        }

        if (newRefs.isNotEmpty()) {
            insertBookCategoryCrossRefs(newRefs)
        }
    }

    /**
     * 查询书籍及其分类
     */
    @Transaction
    @Query("SELECT * FROM books ORDER BY id DESC")
    fun getBooksWithCategories(): Flow<List<BookWithCategories>>

    /**
     * 查询分类及其书籍
     */
    @Transaction
    @Query("SELECT * FROM categories ORDER BY id DESC")
    fun getCategoriesWithBooks(): Flow<List<CategoryWithBooks>>
}