package io.github.lycosmic.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 分类实体类
 */
@Entity(tableName = CategoryEntity.TABLE_NAME)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = COL_ID)
    val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(), // 创建时间
) {
    companion object {
        // 表名
        const val TABLE_NAME = "categories"

        // 列名
        const val COL_ID = "id"
    }
}