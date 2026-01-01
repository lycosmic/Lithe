package io.github.lycosmic.lithe.domain.repository

import io.github.lycosmic.lithe.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    /**
     * 获取所有分类
     */
    fun getCategoriesFlow(): Flow<List<CategoryEntity>>

    /**
     * 获取所有分类，排除默认分类
     */
    fun getCategoriesFlowExcludeDefault(): Flow<List<CategoryEntity>>

    /**
     * 根据分类名获取分类数量
     */
    suspend fun countCategoriesByName(categoryName: String): Int

    /**
     * 插入分类
     */
    suspend fun insertCategory(category: CategoryEntity)

    /**
     * 删除分类
     */
    suspend fun deleteCategory(categoryId: Long)

    /**
     * 更新分类
     */
    suspend fun updateCategory(category: CategoryEntity)

    /**
     * 根据 ID 获取分类
     */
    suspend fun getCategoryById(categoryId: Long): CategoryEntity?
}