package io.github.lycosmic.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.lycosmic.domain.model.ReadingProgress
import io.github.lycosmic.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class ProgressRepositoryImpl @Inject constructor(
    @param:ApplicationContext
    private val context: Context
) : ProgressRepository {

    companion object {
        // 阅读进度数据存储文件名
        const val DATASTORE_NAME = "progress"

        const val BOOK_ID_PREFIX = "progress_"
    }

    private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

    override suspend fun saveProgress(
        bookId: Long,
        progress: ReadingProgress
    ): Result<Any> {
        return runCatching {
            context.dataStore.edit { prefs ->
                prefs[intPreferencesKey("${BOOK_ID_PREFIX}${bookId}_${ReadingProgress.KEY_CHAPTER_INDEX}")] =
                    progress.chapterIndex
                prefs[floatPreferencesKey("${BOOK_ID_PREFIX}${bookId}_${ReadingProgress.KEY_CHAPTER_OFFSET}")] =
                    progress.chapterOffset
                prefs[floatPreferencesKey("${BOOK_ID_PREFIX}${bookId}_${ReadingProgress.KEY_TOTAL_PROGRESS}")] =
                    progress.totalProgress
            }
        }
    }

    override suspend fun getBookProgress(bookId: Long): Flow<Result<ReadingProgress>> {
        return context.dataStore.data.map { prefs ->
            Result.success(
                ReadingProgress(
                    chapterIndex = prefs[intPreferencesKey("${BOOK_ID_PREFIX}${bookId}_${ReadingProgress.KEY_CHAPTER_INDEX}")]
                        ?: 0,
                    chapterOffset = prefs[floatPreferencesKey("${BOOK_ID_PREFIX}${bookId}_${ReadingProgress.KEY_CHAPTER_OFFSET}")]
                        ?: 0f,
                    totalProgress = prefs[floatPreferencesKey("${BOOK_ID_PREFIX}${bookId}_${ReadingProgress.KEY_TOTAL_PROGRESS}")]
                        ?: 0f,
                )
            )
        }.catch { e ->
            emit(Result.failure(e))
        }
    }

}