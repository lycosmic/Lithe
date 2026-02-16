package io.github.lycosmic.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import io.github.lycosmic.data.local.entity.BookEntity
import io.github.lycosmic.data.local.entity.HistoryEntity


/**
 * 阅读历史和书籍关系查询结果
 */
data class HistoryWithBook(
    @Embedded
    val history: HistoryEntity,
    @Relation(
        parentColumn = HistoryEntity.COL_BOOK_ID,
        entityColumn = BookEntity.COL_ID
    )
    val book: BookEntity
)