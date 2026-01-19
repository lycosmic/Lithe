package io.github.lycosmic.domain.repository

import io.github.lycosmic.domain.model.ReadingProgress

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
}