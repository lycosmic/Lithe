package io.github.lycosmic.domain.repository

import io.github.lycosmic.domain.model.ReadingProgress
import kotlinx.coroutines.flow.Flow

/**
 * 阅读进度数据仓库
 */
interface ProgressRepository {
    /**
     * 保存阅读进度
     */
    suspend fun saveProgress(bookId: Long, progress: ReadingProgress): Result<Unit>

    /**
     * 读取阅读进度
     */
    suspend fun getBookProgressSync(bookId: Long): Result<ReadingProgress>

    /**
     * 获取书籍阅读进度流
     */
    suspend fun getBookProgressFlow(bookId: Long): Flow<Result<ReadingProgress>>

    /**
     * 删除书籍阅读进度
     */
    suspend fun deleteProgress(bookId: Long): Result<Unit>
}