package io.github.lycosmic.lithe.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import io.github.lycosmic.lithe.data.model.Book


/**
 * 书籍和分类关系表，用于多对多关联书籍和分类
 */
@Entity(
    tableName = BookCategoryCrossRef.TABLE_NAME,
    primaryKeys = [BookCategoryCrossRef.COL_BOOK_ID, BookCategoryCrossRef.COL_CATEGORY_ID], // 联合主键
    foreignKeys = [
        ForeignKey(
            entity = Book::class,
            parentColumns = [Book.COL_ID],
            childColumns = [BookCategoryCrossRef.COL_BOOK_ID],
            onDelete = ForeignKey.CASCADE // 书删了，关联关系自动删
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = [CategoryEntity.COL_ID],
            childColumns = [BookCategoryCrossRef.COL_CATEGORY_ID],
            onDelete = ForeignKey.CASCADE // 分类删了，关联关系自动删
        )
    ],
    // 索引
    indices = [Index(value = [BookCategoryCrossRef.COL_BOOK_ID]), Index(value = [BookCategoryCrossRef.COL_CATEGORY_ID])]
)
data class BookCategoryCrossRef(
    @ColumnInfo(name = COL_BOOK_ID) val bookId: Long,
    @ColumnInfo(name = COL_CATEGORY_ID) val categoryId: Long
) {
    companion object {
        // 表名
        const val TABLE_NAME = "book_category_cross_ref"

        // 列名
        const val COL_BOOK_ID = "book_id"
        const val COL_CATEGORY_ID = "category_id"
    }
}