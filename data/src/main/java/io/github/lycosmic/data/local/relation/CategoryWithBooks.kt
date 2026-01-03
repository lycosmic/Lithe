package io.github.lycosmic.data.local.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import io.github.lycosmic.data.local.entity.BookCategoryCrossRef
import io.github.lycosmic.data.local.entity.BookEntity
import io.github.lycosmic.data.local.entity.CategoryEntity

/**
 * 分类和书籍关系查询结果
 */
data class CategoryWithBooks(
    @Embedded
    val category: CategoryEntity,
    // 分类下的所有书籍
    @Relation(
        parentColumn = CategoryEntity.COL_ID,
        entityColumn = BookEntity.COL_ID,
        associateBy = Junction(
            // 指定中间表
            value = BookCategoryCrossRef::class,
            parentColumn = BookCategoryCrossRef.COL_CATEGORY_ID,
            entityColumn = BookCategoryCrossRef.COL_BOOK_ID,
        )
    )
    val bookEntities: List<BookEntity>
)