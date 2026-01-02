package io.github.lycosmic.lithe.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = ChapterEntity.TABLE_NAME)
data class ChapterEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COL_BOOK_ID)
    val bookId: Long,
    @ColumnInfo(name = COL_INDEX)
    val index: Int, // 章节序号
    @ColumnInfo(name = COL_TITLE)
    val title: String, // 章节标题
    @ColumnInfo(name = COL_START_OFFSET)
    val startOffset: Long, // TXT专用：起始字节位置
    @ColumnInfo(name = COL_END_OFFSET)
    val endOffset: Long, // TXT专用：结束字节位置
    @ColumnInfo(name = COL_HREF)
    val href: String?, // EPUB专用：HTML 文件路径
) {
    companion object {
        const val TABLE_NAME = "chapters"

        const val COL_BOOK_ID = "book_id"
        const val COL_INDEX = "index"
        const val COL_TITLE = "title"
        const val COL_START_OFFSET = "start_offset"
        const val COL_END_OFFSET = "end_offset"
        const val COL_HREF = "href"
    }
}
