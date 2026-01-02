package io.github.lycosmic.repository

import io.github.lycosmic.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    /**
     * 获取所有分类
     */
    fun getCategoriesFlow(): Flow<List<Category>>

    /**
     * 根据分类名获取分类数量
     */
    suspend fun countCategoriesByName(categoryName: String): Int

    /**
     * 插入分类
     */
    suspend fun insertCategory(category: Category)

    /**
     * 删除分类
     */
    suspend fun deleteCategory(categoryId: Long)

    /**
     * 更新分类
     */
    suspend fun updateCategory(category: Category)

    /**
     * 根据 ID 获取分类
     */
    suspend fun getCategoryById(categoryId: Long): Category?

    /**
     * 确保默认分类存在
     */
    suspend fun ensureDefaultCategoryExists()
}