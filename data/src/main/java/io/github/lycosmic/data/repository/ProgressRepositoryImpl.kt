package io.github.lycosmic.data.repository

import io.github.lycosmic.data.local.dao.BookProgressDao
import io.github.lycosmic.data.mapper.toDomain
import io.github.lycosmic.data.mapper.toEntity
import io.github.lycosmic.domain.model.ReadingProgress
import io.github.lycosmic.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class ProgressRepositoryImpl @Inject constructor(
    private val bookProgressDao: BookProgressDao
) : ProgressRepository {

    override suspend fun saveProgress(
        bookId: Long,
        progress: ReadingProgress
    ): Result<Any> {
        return runCatching {
            val entity = progress.toEntity()
            bookProgressDao.saveProgress(entity)
        }
    }

    override suspend fun getBookProgress(bookId: Long): Flow<Result<ReadingProgress>> {
        return bookProgressDao.getProgressFlow(bookId).map { entity ->
            if (entity == null) {
                // 默认返回一个空的进度
                return@map Result.success(ReadingProgress(bookId, 0, 0))
            }
            return@map Result.success(entity.toDomain())
        }.catch { e ->
            emit(Result.failure(exception = e))
        }
    }

}