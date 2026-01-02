package io.github.lycosmic.lithe.data.mapper

import io.github.lycosmic.lithe.data.local.entity.BookEntity
import io.github.lycosmic.lithe.data.local.entity.CategoryEntity
import io.github.lycosmic.lithe.data.local.relation.BookWithCategories
import io.github.lycosmic.model.Book
import io.github.lycosmic.model.FileFormat


fun BookEntity.toDomain(categories: List<CategoryEntity> = emptyList()): Book {
    return Book(
        id = id,
        uniqueId = uniqueId,
        title = title,
        author = author.split(","),
        description = description,
        language = language,
        publisher = publisher,
        subjects = subjects ?: emptyList(),
        fileSize = fileSize,
        fileUri = fileUri,
        format = FileFormat.fromValue(format),
        coverPath = coverPath,
        lastReadPosition = lastReadPosition,
        progress = progress,
        lastReadTime = lastReadTime,
        importTime = importTime,
        categories = categories.map { it.toDomain() }
    )
}

fun Book.toEntity(): BookEntity {
    return BookEntity(
        id = id,
        uniqueId = uniqueId,
        title = title,
        author = author.joinToString(","),
        description = description,
        language = language,
        publisher = publisher,
        subjects = subjects,
        fileSize = fileSize,
        fileUri = fileUri,
        format = format.value,
        coverPath = coverPath,
        lastReadPosition = lastReadPosition,
        progress = progress,
        lastReadTime = lastReadTime,
        importTime = importTime
    )
}

fun BookWithCategories.toDomain(): Book {
    return this.bookEntity.toDomain(
        categories = this.categories
    )
}