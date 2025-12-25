package io.github.lycosmic.lithe.presentation.settings.library

import io.github.lycosmic.lithe.data.local.entity.CategoryEntity

sealed class LibrarySettingsEffect {
    /**
     * 导航至上一级
     */
    object NavigateBack : LibrarySettingsEffect()

    /**
     * 打开创建分类对话框
     */
    object OpenCreateCategoryDialog : LibrarySettingsEffect()

    /**
     * 显示分类名称已存在提示
     */
    object CategoryNameExists : LibrarySettingsEffect()

    /**
     * 打开编辑分类名称对话框
     */
    data class OpenEditCategoryDialog(val category: CategoryEntity) : LibrarySettingsEffect()

    /**
     * 打开删除分类对话框
     */
    data class OpenDeleteCategoryDialog(val categoryId: Long) : LibrarySettingsEffect()
}