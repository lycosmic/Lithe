package io.github.lycosmic.lithe.presentation.settings.library

sealed class LibrarySettingsEffect {
    /**
     * 导航至上一级
     */
    object NavigateBack : LibrarySettingsEffect()
}