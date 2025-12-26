package io.github.lycosmic.lithe.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.lycosmic.lithe.data.model.Constants

const val CATEGORY_TABLE_NAME = "categories"
/**
 * 分类实体类
 */
@Entity(tableName = CATEGORY_TABLE_NAME)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = Constants.DEFAULT_CATEGORY_ID,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(), // 创建时间
)