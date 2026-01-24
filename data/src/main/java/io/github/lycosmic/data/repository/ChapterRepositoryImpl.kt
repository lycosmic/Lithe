package io.github.lycosmic.data.repository

import io.github.lycosmic.data.local.dao.ChapterDao
import io.github.lycosmic.data.mapper.toDomain
import io.github.lycosmic.data.mapper.toEntity
import io.github.lycosmic.domain.model.BookChapter
import io.github.lycosmic.domain.repository.ChapterRepository
import javax.inject.Inject

class ChapterRepositoryImpl @Inject constructor(
    private val chapterDao: ChapterDao
) : ChapterRepository {
    override suspend fun getChaptersByBookId(bookId: Long): Result<List<BookChapter>> {
        return runCatching {
            val chapters = chapterDao.getChaptersByBookId(bookId).map {
                it.toDomain()
            }
            return if (chapters.isNotEmpty()) {
                Result.success(chapters)
            } else {
                Result.failure(Exception("找不到该书籍的章节列表"))
            }
        }
    }

    override suspend fun saveChapters(
        spine: List<BookChapter>
    ): Result<List<Long>> {
        return runCatching {
            chapterDao.insertChapters(spine.map {
                it.toEntity()
            })
        }
    }

    override suspend fun deleteChaptersByBookId(bookId: Long): Result<Unit> {
        return runCatching {
            chapterDao.deleteChaptersByBookId(bookId)
        }
    }

}