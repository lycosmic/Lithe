package io.github.lycosmic.data.repository

import io.github.lycosmic.data.local.dao.BookProgressDao
import io.github.lycosmic.data.mapper.toDomain
import io.github.lycosmic.data.mapper.toEntity
import io.github.lycosmic.domain.model.ReadingProgress
import io.github.lycosmic.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class ProgressRepositoryImpl @Inject constructor(
    private val bookProgressDao: BookProgressDao
) : ProgressRepository {

    override suspend fun saveProgress(
        bookId: Long,
        progress: ReadingProgress
    ): Result<Unit> {
        return runCatching {
            val entity = progress.toEntity()
            bookProgressDao.saveProgress(entity)
        }
    }

    override suspend fun getBookProgressSync(bookId: Long): Result<ReadingProgress> {
        return runCatching {
            bookProgressDao.getProgress(bookId)?.toDomain()
            // 默认返回一个空的进度
                ?: ReadingProgress(bookId, 0, 0, 0f)
        }
    }

    override suspend fun getBookProgressFlow(bookId: Long): Flow<Result<ReadingProgress>> {
        return bookProgressDao.getProgressFlow(bookId).filterNotNull().map {
            runCatching {
                it.toDomain()
            }
        }
    }

    override suspend fun deleteProgress(bookId: Long): Result<Unit> {
        return runCatching {
            bookProgressDao.deleteProgress(bookId)
        }
    }

}