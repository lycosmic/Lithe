package io.github.lycosmic.lithe.presentation.detail

sealed class BookDetailEvent {
    /**
     * 按下返回键, 回到书库页面
     */
    data object OnBackClicked : BookDetailEvent()

    /**
     * 点击移动书籍分类
     */
    data object OnMoveClicked : BookDetailEvent()

    /**
     * 点击删除书籍按钮
     */
    data object OnDeleteClicked : BookDetailEvent()

    /**
     * 点击确认删除书籍按钮
     */
    data object OnConfirmDeleteClicked : BookDetailEvent()

    /**
     * 点击取消删除书籍按钮
     */
    data object OnCancelDeleteClicked : BookDetailEvent()

    /**
     * 点击删除书籍对话框外部
     */
    data object OnDeleteDialogDismissed : BookDetailEvent()
}