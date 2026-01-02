package io.github.lycosmic.lithe.data.mapper

import io.github.lycosmic.lithe.data.local.entity.CategoryEntity
import io.github.lycosmic.model.Category


fun CategoryEntity.toDomain(): Category {
    return Category(
        id = id,
        name = name,
        createdAt = createdAt
    )
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        createdAt = createdAt
    )
}