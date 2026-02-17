package io.github.lycosmic.data.mapper

import io.github.lycosmic.data.local.entity.BookEntity
import io.github.lycosmic.data.local.entity.CategoryEntity
import io.github.lycosmic.data.local.relation.BookWithCategories
import io.github.lycosmic.domain.model.Book
import io.github.lycosmic.domain.model.FileFormat


fun BookEntity.toDomain(categories: List<CategoryEntity> = emptyList()): Book {
    return Book(
        id = id,
        title = title,
        author = author.split(","),
        description = description,
        fileSize = fileSize,
        fileUri = fileUri,
        format = FileFormat.fromValue(format),
        coverPath = coverPath,
        importTime = importTime,
        categories = categories.map { it.toDomain() },
        progress = 0f,
        charset = charsetName
    )
}

fun Book.toEntity(): BookEntity {
    return BookEntity(
        id = id,
        title = title,
        author = author.joinToString(","),
        description = description,
        fileSize = fileSize,
        fileUri = fileUri,
        format = format.value,
        coverPath = coverPath,
        importTime = importTime,
        language = null,
        publisher = null,
        subjects = emptyList(),
        charsetName = charset
    )
}

fun BookWithCategories.toDomain(): Book {
    return this.bookEntity.toDomain(
        categories = this.categories
    )
}