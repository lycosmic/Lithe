package io.github.lycosmic.data.mapper

import io.github.lycosmic.data.local.entity.HistoryEntity
import io.github.lycosmic.domain.model.Book
import io.github.lycosmic.domain.model.ReadHistory


fun HistoryEntity.toDomain(book: Book): ReadHistory {
    return ReadHistory(
        id = id,
        book = book,
        time = time
    )
}


fun ReadHistory.toEntity(): HistoryEntity {
    return HistoryEntity(
        id = id,
        bookId = book.id,
        time = time
    )
}