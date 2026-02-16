package io.github.lycosmic.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = HistoryEntity.TABLE_NAME)
data class HistoryEntity(
    @PrimaryKey(true)
    @ColumnInfo(name = COL_ID)
    val id: Int = 0,
    @ColumnInfo(name = COL_BOOK_ID)
    val bookId: Long,
    @ColumnInfo(name = COL_TIME)
    val time: Long // 阅读时间
) {
    companion object {
        const val TABLE_NAME = "history"

        const val COL_ID = "id"

        const val COL_BOOK_ID = "book_id"

        const val COL_TIME = "time"
    }
}