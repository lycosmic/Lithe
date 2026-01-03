package io.github.lycosmic.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import io.github.lycosmic.data.local.entity.CategoryEntity
import io.github.lycosmic.model.Category
import kotlinx.coroutines.flow.Flow


@Dao
interface CategoryDao {
    /**
     * 插入分类
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    /**
     * 获取所有分类 (返回 Flow)
     */
    @Query("SELECT * FROM categories ORDER BY createdAt DESC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    /**
     * 获取所有分类，不包括默认分类 (返回 Flow)
     */
    @Query("SELECT * FROM categories WHERE id != ${Category.DEFAULT_CATEGORY_ID} ORDER BY createdAt DESC")
    fun getAllCategoriesExcludeDefault(): Flow<List<CategoryEntity>>

    /**
     * 获取所有分类 (返回 List)
     */
    @Query("SELECT * FROM categories ORDER BY createdAt DESC")
    suspend fun getAllCategoriesSync(): List<CategoryEntity>

    /**
     * 通过名称获取分类数量
     */
    @Query("SELECT COUNT(*) FROM categories WHERE name = :name")
    suspend fun countCategoriesByName(name: String): Int

    /**
     * 更新分类
     */
    @Update
    suspend fun updateCategory(category: CategoryEntity)

    /**
     * 删除分类
     */
    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteCategory(id: Long)

    /**
     * 根据分类 ID 获取分类
     */
    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Long): CategoryEntity?

    /**
     * 确保默认分类存在
     */
    @Transaction
    suspend fun ensureDefaultCategory() {
        val defaultCategory = getCategoryById(Category.DEFAULT_CATEGORY_ID)
        if (defaultCategory == null) {
            // 插入默认分类
            val category = CategoryEntity(
                id = Category.DEFAULT_CATEGORY_ID,
                name = "",
                createdAt = System.currentTimeMillis()
            )
            insertCategory(category)
        }
    }
}