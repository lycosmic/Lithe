package io.github.lycosmic.data.mapper

import io.github.lycosmic.data.local.entity.ChapterEntity
import io.github.lycosmic.domain.model.BookChapter


fun ChapterEntity.toDomain(): BookChapter {
    return when (type) {
        ChapterEntity.TYPE_EPUB -> BookChapter.EpubChapter(
            bookId = bookId,
            index = index,
            title = title,
            href = href ?: "",
            fileSizeBytes = length
        )

        else -> BookChapter.TxtChapter(
            bookId = bookId,
            index = index,
            title = title,
            startOffset = startOffset ?: 0L,
            endOffset = endOffset ?: 0L,
            fileSizeBytes = length
        )
    }
}

fun BookChapter.toEntity(): ChapterEntity {
    return when (this) {
        is BookChapter.EpubChapter -> ChapterEntity(
            bookId = bookId,
            index = index,
            title = title,
            type = ChapterEntity.TYPE_EPUB,
            href = href,
            startOffset = null,
            endOffset = null,
            length = fileSizeBytes
        )

        is BookChapter.TxtChapter -> ChapterEntity(
            bookId = bookId,
            index = index,
            title = title,
            type = ChapterEntity.TYPE_TXT,
            startOffset = startOffset,
            endOffset = endOffset,
            href = null,
            length = fileSizeBytes
        )
    }
}