package io.github.lycosmic.lithe.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.github.lycosmic.lithe.data.local.entity.CategoryEntity
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
    @Query("SELECT * FROM categories WHERE id != ${CategoryEntity.DEFAULT_CATEGORY_ID} ORDER BY createdAt DESC")
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

}