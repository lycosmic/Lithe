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
    suspend fun saveProgress(bookId: Long, progress: ReadingProgress): Result<Any>

    /**
     * 读取阅读进度
     */
    suspend fun getBookProgress(bookId: Long): Flow<Result<ReadingProgress>>
}