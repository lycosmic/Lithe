package io.github.lycosmic.domain.repository

import io.github.lycosmic.domain.model.ReadHistory
import kotlinx.coroutines.flow.Flow

interface ReadHistoryRepository {
    /**
     * 获取指定书籍的阅读历史
     */
    suspend fun getHistoryByBookId(
        bookId: Int
    ): Result<ReadHistory>

    /**
     * 添加阅读历史
     */
    suspend fun addHistory(
        history: ReadHistory
    ): Result<Unit>


    /**
     * 获取所有阅读历史
     */
    suspend fun getHistories(): Flow<List<ReadHistory>>

    /**
     * 删除所有阅读历史
     */
    suspend fun deleteWholeHistory(): Result<Unit>

    /**
     * 删除指定书籍的阅读历史
     */
    suspend fun deleteHistoryByBookId(
        bookId: Int
    ): Result<Unit>

    suspend fun deleteHistory(
        history: ReadHistory
    ): Result<Unit>

}