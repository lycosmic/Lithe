package io.github.lycosmic.lithe.data.repository

import io.github.lycosmic.model.BookSpineItem
import io.github.lycosmic.repository.ChapterRepository
import javax.inject.Inject

class ChapterRepositoryImpl @Inject constructor() : ChapterRepository {
    override suspend fun getChaptersByBookId(bookId: Long): List<BookSpineItem> {
        return emptyList()
    }

    override suspend fun saveChapters(
        bookId: Long,
        spine: List<BookSpineItem>
    ) {

    }

}