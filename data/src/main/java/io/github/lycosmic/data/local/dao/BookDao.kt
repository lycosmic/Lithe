package io.github.lycosmic.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import io.github.lycosmic.data.local.entity.BookCategoryCrossRef
import io.github.lycosmic.data.local.entity.BookEntity
import io.github.lycosmic.data.local.entity.CategoryEntity
import io.github.lycosmic.data.local.relation.BookWithCategories
import io.github.lycosmic.data.local.relation.CategoryWithBooks
import io.github.lycosmic.domain.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {

    @Query("SELECT * FROM ${BookEntity.TABLE_NAME} ORDER BY ${BookEntity.COL_IMPORT_TIME} DESC")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM ${BookEntity.TABLE_NAME} WHERE ${BookEntity.COL_ID} = :id LIMIT 1")
    suspend fun getBookById(id: Long): BookEntity?

    @Query("SELECT * FROM ${BookEntity.TABLE_NAME} WHERE ${BookEntity.COL_ID} = :id LIMIT 1")
    fun getBookFlowById(id: Long): Flow<BookEntity?>

    /**
     * 插入书籍，返回新书的 ID
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBook(bookEntity: BookEntity): Long

    @Delete
    suspend fun deleteBook(bookEntity: BookEntity)

    @Query("DELETE FROM ${BookEntity.TABLE_NAME} WHERE ${BookEntity.COL_ID}=:id")
    suspend fun deleteBookById(id: Long)

    @Query("SELECT * FROM ${BookEntity.TABLE_NAME} WHERE  ${BookEntity.COL_FILE_URI} = :uri LIMIT 1")
    suspend fun getBookByUri(uri: String): BookEntity?

    /**
     * 插入书籍分类关联
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBookCategoryCrossRefs(refs: List<BookCategoryCrossRef>)

    /**
     * 删除某本书的所有分类关联，用于更新分类时，先清空再重新插入
     */
    @Query("DELETE FROM ${BookCategoryCrossRef.TABLE_NAME} WHERE ${BookCategoryCrossRef.COL_BOOK_ID} = :bookId")
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
     * 获取书籍的分类
     */
    @Query("SELECT * FROM ${CategoryEntity.TABLE_NAME} WHERE ${CategoryEntity.COL_ID} IN (SELECT ${BookCategoryCrossRef.COL_CATEGORY_ID} FROM ${BookCategoryCrossRef.TABLE_NAME} WHERE ${BookCategoryCrossRef.COL_BOOK_ID} = :bookId)")
    fun getBookCategoriesFlow(bookId: Long): Flow<List<Category>>

    /**
     * 查询书籍及其分类
     */
    @Transaction
    @Query("SELECT * FROM ${BookEntity.TABLE_NAME} ORDER BY ${BookEntity.COL_ID} DESC")
    fun getBooksWithCategories(): Flow<List<BookWithCategories>>

    /**
     * 查询分类及其书籍
     */
    @Transaction
    @Query("SELECT * FROM ${CategoryEntity.TABLE_NAME} ORDER BY ${CategoryEntity.COL_ID} DESC")
    fun getCategoriesWithBooks(): Flow<List<CategoryWithBooks>>
}