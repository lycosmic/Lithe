package io.github.lycosmic.use_case.settings.library

import io.github.lycosmic.model.Category
import io.github.lycosmic.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


/**
 * 获取所有分类，但不包括默认分类
 */
class GetCategoriesExcludingDefaultUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(): Flow<Result<List<Category>>> {
        // 不处理异常
        return categoryRepository.getCategoriesFlow().map { result ->
            result.map { list ->
                list.filter {
                    // 筛选出非默认分类
                    it.id != Category.DEFAULT_CATEGORY_ID
                }
            }
        }
    }
}