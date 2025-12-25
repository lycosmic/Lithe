package io.github.lycosmic.lithe.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * 分类实体类
 */
@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(), // 创建时间
)