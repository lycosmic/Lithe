package io.github.lycosmic.data.repository

import io.github.lycosmic.data.local.dao.HistoryDao
import io.github.lycosmic.data.mapper.toDomain
import io.github.lycosmic.data.mapper.toEntity
import io.github.lycosmic.domain.model.ReadHistory
import io.github.lycosmic.domain.repository.ReadHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class ReadHistoryRepositoryImpl @Inject constructor(
    private val historyDao: HistoryDao,
) : ReadHistoryRepository {
    override suspend fun getHistoryByBookId(bookId: Int): Result<ReadHistory> {
        return runCatching {
            val historyWithBook =
                historyDao.getLastHistoryByBookId(bookId = bookId) ?: return Result.failure(
                    Exception("No history found for bookId $bookId")
                )

            historyWithBook.let {
                it.history.toDomain(it.book.toDomain())
            }
        }
    }

    override suspend fun addHistory(history: ReadHistory): Result<Unit> {
        return runCatching {
            historyDao.insertHistory(history.toEntity())
        }
    }

    override suspend fun getHistories(): Flow<List<ReadHistory>> {
        return historyDao.getHistories().map { historyWithBookList ->
            historyWithBookList.map { historyWithBook ->
                historyWithBook.let {
                    it.history.toDomain(it.book.toDomain())
                }
            }
        }
    }

    override suspend fun deleteWholeHistory(): Result<Unit> {
        return runCatching {
            historyDao.deleteWholeHistory()
        }
    }

    override suspend fun deleteHistoryByBookId(bookId: Int): Result<Unit> {
        return runCatching {
            historyDao.deleteHistoryByBookId(bookId)
        }
    }

    override suspend fun deleteHistory(history: ReadHistory): Result<Unit> {
        return runCatching {
            historyDao.deleteHistory(history.toEntity())
        }
    }


}