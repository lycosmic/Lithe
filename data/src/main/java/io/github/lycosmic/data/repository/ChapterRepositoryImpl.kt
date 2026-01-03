package io.github.lycosmic.data.repository

import io.github.lycosmic.domain.model.BookChapter
import io.github.lycosmic.domain.repository.ChapterRepository
import javax.inject.Inject

class ChapterRepositoryImpl @Inject constructor() : ChapterRepository {
    override suspend fun getChaptersByBookId(bookId: Long): List<BookChapter> {
        return emptyList()
    }

    override suspend fun saveChapters(
        bookId: Long,
        spine: List<BookChapter>
    ) {

    }

}