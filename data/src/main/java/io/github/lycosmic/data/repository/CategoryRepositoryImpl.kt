package io.github.lycosmic.data.repository

import io.github.lycosmic.data.local.dao.CategoryDao
import io.github.lycosmic.data.mapper.toDomain
import io.github.lycosmic.data.mapper.toEntity
import io.github.lycosmic.domain.model.Category
import io.github.lycosmic.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getCategoriesFlow(): Flow<Result<List<Category>>> {
        return categoryDao.getAllCategories().map { entities ->
            val books = entities.map {
                it.toDomain()
            }
            Result.success(books)
        }.catch { e ->
            // 捕获上游异常
            emit(Result.failure(e))
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

    override suspend fun ensureDefaultCategoryExists() {
        categoryDao.ensureDefaultCategory()
    }
}