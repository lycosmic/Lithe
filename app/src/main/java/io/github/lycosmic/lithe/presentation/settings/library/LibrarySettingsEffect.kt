package io.github.lycosmic.lithe.presentation.settings.library

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
}