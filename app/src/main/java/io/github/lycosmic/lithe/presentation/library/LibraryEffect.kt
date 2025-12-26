package io.github.lycosmic.lithe.presentation.library

sealed class LibraryEffect {

    /**
     * 导航到设置页面
     */
    data object OnNavigateToSettings : LibraryEffect()

    /**
     * 导航到关于页面
     */
    data object OnNavigateToAbout : LibraryEffect()

    /**
     * 导航到帮助页面
     */
    data object OnNavigateToHelp : LibraryEffect()

    /**
     * 导航到书籍详情页面
     */
    data class OnNavigateToBookDetail(val bookId: Long) : LibraryEffect()

    /**
     * 导航到书籍阅读页面
     */
    data class OnNavigateToBookRead(val bookId: Long) : LibraryEffect()

    /**
     * 导航到书籍浏览页面
     */
    data object OnNavigateToBrowser : LibraryEffect()

    /**
     * 打开筛选面板
     */
    data object OpenFilterBottomSheet : LibraryEffect()

    /**
     * 关闭筛选面板
     */
    data object CloseFilterBottomSheet : LibraryEffect()

    /**
     * 打开更多选项面板
     */
    data object OpenMoreOptionsBottomSheet : LibraryEffect()

    /**
     * 关闭更多选项面板
     */
    data object CloseMoreOptionsBottomSheet : LibraryEffect()

    /**
     * 显示删除书籍确认对话框
     */
    data object ShowDeleteBookConfirmDialog : LibraryEffect()

    /**
     * 关闭删除书籍确认对话框
     */
    data object CloseDeleteBookConfirmDialog : LibraryEffect()

    /**
     * 显示成功删除所选提示
     */
    data object ShowDeleteBookSuccessToast : LibraryEffect()

    /**
     * 显示成功删除的书籍数提示
     */
    data class ShowDeleteBookCountToast(val count: Int) : LibraryEffect()
}