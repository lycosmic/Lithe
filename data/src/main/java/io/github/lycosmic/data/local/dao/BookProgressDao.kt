package io.github.lycosmic.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.lycosmic.data.local.entity.BookProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookProgressDao {
    /**
     * 插入或更新阅读进度
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(entity: BookProgressEntity)

    /**
     * 获取阅读进度
     */
    @Query("SELECT * FROM ${BookProgressEntity.TABLE_NAME} WHERE ${BookProgressEntity.COL_BOOK_ID} = :bookId")
    fun getProgressFlow(bookId: Long): Flow<BookProgressEntity?>

    /**
     * 获取阅读进度
     */
    @Query("SELECT * FROM ${BookProgressEntity.TABLE_NAME} WHERE ${BookProgressEntity.COL_BOOK_ID} = :bookId")
    suspend fun getProgress(bookId: Long): BookProgressEntity?

    /**
     * 删除阅读进度
     */
    @Query("DELETE FROM ${BookProgressEntity.TABLE_NAME} WHERE ${BookProgressEntity.COL_BOOK_ID} = :bookId")
    suspend fun deleteProgress(bookId: Long)
}