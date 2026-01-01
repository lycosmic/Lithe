package io.github.lycosmic.lithe.domain.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import io.github.lycosmic.lithe.data.local.entity.BookCategoryCrossRef
import io.github.lycosmic.lithe.data.local.entity.BookEntity
import io.github.lycosmic.lithe.data.local.entity.CategoryEntity

/**
 * 书籍和分类关系查询结果
 */
data class BookWithCategories(
    @Embedded
    val bookEntity: BookEntity,
    @Relation(
        parentColumn = BookEntity.COL_ID, // 书籍表
        entityColumn = CategoryEntity.COL_ID, // 分类表
        associateBy = Junction( // 指定中间表
            value = BookCategoryCrossRef::class,
            parentColumn = BookCategoryCrossRef.COL_BOOK_ID,
            entityColumn = BookCategoryCrossRef.COL_CATEGORY_ID
        )
    )
    val categories: List<CategoryEntity>
)