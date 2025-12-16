package io.github.lycosmic.lithe.presentation.detail

import androidx.annotation.StringRes

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

    /**
     * 关闭移动书籍分类对话框
     */
    data object DismissMoveBookDialog : BookDetailEffect()

    /**
     * 关闭删除书籍对话框
     */
    data object DismissDeleteBookDialog : BookDetailEffect()

    /**
     * 书籍成功删除，显示 Toast
     */
    data class ShowBookDeletedToast(@param:StringRes val messageResId: Int) : BookDetailEffect()
}