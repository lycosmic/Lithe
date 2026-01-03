package io.github.lycosmic.data.mapper

import io.github.lycosmic.data.local.entity.ChapterEntity
import io.github.lycosmic.domain.model.BookChapter
import io.github.lycosmic.domain.model.EpubChapter
import io.github.lycosmic.domain.model.TxtChapter


fun ChapterEntity.toDomain(): BookChapter {
    return when (type) {
        ChapterEntity.TYPE_EPUB -> EpubChapter(
            bookId = bookId,
            index = index,
            title = title,
            href = href ?: ""
        )

        else -> TxtChapter(
            bookId = bookId,
            index = index,
            title = title,
            startOffset = startOffset ?: 0L,
            endOffset = endOffset ?: 0L
        )
    }
}

fun BookChapter.toEntity(): ChapterEntity {
    return when (this) {
        is EpubChapter -> ChapterEntity(
            bookId = bookId,
            index = index,
            title = title,
            type = ChapterEntity.TYPE_EPUB,
            href = href,
            startOffset = null,
            endOffset = null
        )

        is TxtChapter -> ChapterEntity(
            bookId = bookId,
            index = index,
            title = title,
            type = ChapterEntity.TYPE_TXT,
            startOffset = startOffset,
            endOffset = endOffset,
            href = null
        )
    }
}