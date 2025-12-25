package io.github.lycosmic.lithe.data.repository

import io.github.lycosmic.lithe.data.local.dao.CategoryDao
import io.github.lycosmic.lithe.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {
    fun getCategories(): Flow<List<CategoryEntity>> {
        return categoryDao.getAllCategories()
    }

    suspend fun countCategoriesByName(categoryName: String): Int {
        return categoryDao.countCategoriesByName(categoryName)
    }

    suspend fun insertCategory(category: CategoryEntity) {
        categoryDao.insertCategory(category)
    }

    suspend fun deleteCategory(categoryId: Long) {
        categoryDao.deleteCategory(categoryId)
    }

    suspend fun updateCategory(category: CategoryEntity) {
        categoryDao.updateCategory(category)
    }

    suspend fun getCategoryById(categoryId: Long): CategoryEntity? {
        return categoryDao.getCategoryById(categoryId)
    }
}