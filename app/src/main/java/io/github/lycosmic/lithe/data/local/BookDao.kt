package io.github.lycosmic.lithe.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.lycosmic.lithe.data.model.Book
import kotlinx.coroutines.flow.Flow


@Dao
interface BookDao {

    @Query("SELECT * FROM books ORDER BY lastReadTime DESC")
    fun getAllBooks(): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE id = :id LIMIT 1")
    suspend fun getBookById(id: Long): Book?

    @Query("SELECT * FROM books WHERE id = :id LIMIT 1")
    fun getBookFlowById(id: Long): Flow<Book?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book): Long

    @Delete
    suspend fun deleteBook(book: Book)

    @Query("DELETE FROM books WHERE id=:id")
    suspend fun deleteBookById(id: Long)

    @Query("SELECT * FROM books WHERE uniqueId = :uniqueId LIMIT 1")
    suspend fun getBookByUniqueId(uniqueId: String): Book?
}