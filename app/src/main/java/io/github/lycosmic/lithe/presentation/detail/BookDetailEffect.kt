package io.github.lycosmic.lithe.presentation.detail

sealed class BookDetailEffect {

    /**
     * 导航返回
     */
    data object NavigateBack : BookDetailEffect()

}