package io.github.lycosmic.lithe.presentation.history


sealed class HistoryEffect {

    /**
     * 显示删除所有阅读记录的对话框
     */
    data object OnDeleteWholeHistoryDialogShow : HistoryEffect()

    /**
     * 隐藏删除所有阅读记录的对话框
     */
    data object OnDeleteWholeHistoryDialogDismiss : HistoryEffect()

    /**
     * 跳转到书籍阅读页面
     */
    data class NavigateToBookReading(val bookId: Long) : HistoryEffect()

    /**
     * 跳转到书籍详情页面
     */
    data class NavigateToBookDetail(val bookId: Long) : HistoryEffect()

    /**
     * 跳转到设置页面
     */
    data object NavigateToSettings : HistoryEffect()

    /**
     * 显示更多底部弹窗
     */
    data object OnMoreBottomSheetShow : HistoryEffect()

    /**
     * 隐藏更多底部弹窗
     */
    data object OnMoreBottomSheetDismiss : HistoryEffect()
}