package io.github.lycosmic.lithe.presentation.detail

sealed class BookDetailEffect {

    /**
     * 导航返回
     */
    data object NavigateBack : BookDetailEffect()

    /**
     * 打开移动书籍分类对话框
     */
    data object ShowMoveBookDialog : BookDetailEffect()

    /**
     * 打开删除书籍对话框
     */
    data object ShowDeleteBookDialog : BookDetailEffect()

}