package io.github.lycosmic.data.mapper

import io.github.lycosmic.data.local.entity.BookProgressEntity
import io.github.lycosmic.domain.model.ReadingProgress


fun ReadingProgress.toEntity() = BookProgressEntity(
    bookId = bookId,
    chapterIndex = chapterIndex,
    charIndex = chapterOffsetCharIndex,
    updatedAt = System.currentTimeMillis(),
    progress = progressPercent
)

fun BookProgressEntity.toDomain() = ReadingProgress(
    bookId = bookId,
    chapterIndex = chapterIndex,
    chapterOffsetCharIndex = charIndex,
    progressPercent = progress
)