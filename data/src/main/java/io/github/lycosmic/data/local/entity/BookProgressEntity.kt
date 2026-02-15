package io.github.lycosmic.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = BookProgressEntity.TABLE_NAME)
data class BookProgressEntity(
    @PrimaryKey
    @ColumnInfo(name = COL_BOOK_ID)
    val bookId: Long,
    @ColumnInfo(name = COL_CHAPTER_INDEX)
    val chapterIndex: Int,
    @ColumnInfo(name = COL_CHAR_INDEX)
    val charIndex: Int,
    @ColumnInfo(name = COL_PROGRESS)
    val progress: Float = 0f,
    @ColumnInfo(name = COL_UPDATED_AT)
    val updatedAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = COL_ITEM_INDEX)
    val itemIndex: Int = 0,
    @ColumnInfo(name = COL_ITEM_OFFSET)
    val itemOffset: Int = 0
) {
    companion object {
        const val TABLE_NAME = "book_progress"

        const val COL_BOOK_ID = "book_id"
        const val COL_CHAPTER_INDEX = "chapter_index"
        const val COL_CHAR_INDEX = "char_index"
        const val COL_PROGRESS = "progress"
        const val COL_UPDATED_AT = "updated_at"
        const val COL_ITEM_INDEX = "lazy_column_item_index"
        const val COL_ITEM_OFFSET = "lazy_column_item_offset"
    }
}
