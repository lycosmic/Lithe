package io.github.lycosmic.lithe.data.repository

import io.github.lycosmic.lithe.data.local.dao.CategoryDao
import io.github.lycosmic.lithe.data.mapper.toDomain
import io.github.lycosmic.lithe.data.mapper.toEntity
import io.github.lycosmic.model.Category
import io.github.lycosmic.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getCategoriesFlow(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map {
            it.map { categoryEntity ->
                categoryEntity.toDomain()
            }
        }
    }

    override suspend fun countCategoriesByName(categoryName: String): Int {
        return categoryDao.countCategoriesByName(categoryName)
    }

    override suspend fun insertCategory(category: Category) {
        categoryDao.insertCategory(category.toEntity())
    }

    override suspend fun deleteCategory(categoryId: Long) {
        categoryDao.deleteCategory(categoryId)
    }

    override suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category.toEntity())
    }

    override suspend fun getCategoryById(categoryId: Long): Category? {
        return categoryDao.getCategoryById(categoryId)?.toDomain()
    }
}