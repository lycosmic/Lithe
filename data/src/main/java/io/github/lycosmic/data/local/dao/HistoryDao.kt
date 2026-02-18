package io.github.lycosmic.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import io.github.lycosmic.data.local.entity.HistoryEntity
import io.github.lycosmic.data.local.relation.HistoryWithBook
import kotlinx.coroutines.flow.Flow


@Dao
interface HistoryDao {
    /**
     * 获取所有阅读记录
     */
    @Transaction
    @Query("SELECT * FROM ${HistoryEntity.TABLE_NAME}")
    fun getHistories(): Flow<List<HistoryWithBook>>

    /**
     * 获取指定书籍 id 的最新阅读记录
     */
    @Transaction
    @Query("SELECT * FROM ${HistoryEntity.TABLE_NAME} WHERE ${HistoryEntity.COL_BOOK_ID} = :bookId ORDER BY ${HistoryEntity.COL_TIME} DESC LIMIT 1")
    suspend fun getLastHistoryByBookId(bookId: Int): HistoryWithBook?

    /**
     * 添加阅读记录
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity)

    /**
     * 删除所有阅读记录
     */
    @Query("DELETE FROM ${HistoryEntity.TABLE_NAME}")
    suspend fun deleteWholeHistory(): Int

    /**
     * 删除指定书籍 id 的阅读记录
     */
    @Query("DELETE FROM ${HistoryEntity.TABLE_NAME} WHERE ${HistoryEntity.COL_BOOK_ID} = :bookId")
    suspend fun deleteHistoryByBookId(bookId: Long): Int

    /**
     * 删除阅读记录
     */
    @Delete
    suspend fun deleteHistory(history: HistoryEntity): Int
}