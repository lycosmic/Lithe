package io.github.lycosmic.lithe.presentation.reader

sealed class ReaderEffect {
    // --- 导航 ---
    /**
     * 导航回上一级
     */
    object NavigateBack : ReaderEffect()

    // --- 上下栏 ---
    /**
     * 显示或隐藏顶部工具栏和底部控制层
     */
    object ShowOrHideTopBarAndBottomControl : ReaderEffect()

    /**
     * 显示章节列表抽屉
     */
    object ShowChapterListDrawer : ReaderEffect()

    /**
     * 隐藏章节列表抽屉
     */
    object HideChapterListDrawer : ReaderEffect()

    // --- 阅读 ---
    /**
     * 刚打开书或拖动进度条时，滚动到指定位置
     */
    data class ScrollToItem(val index: Int) : ReaderEffect()

    /**
     * 改变字号后，恢复阅读的第一个字在屏幕顶部
     */
    data class RestoreFocus(val charIndex: Int) : ReaderEffect()

    /**
     * 打开章节列表时，滚动到指定章节
     */
    data class ScrollToChapter(val index: Int) : ReaderEffect()

    // -- 提示 ---
    /**
     * 显示当前已为第一章的提示
     */
    data object ShowFirstChapterToast : ReaderEffect()

    /**
     * 显示当前已为最后一章的提示
     */
    data object ShowLastChapterToast : ReaderEffect()

    /**
     * 显示加载章节内容失败的提示
     */
    data object ShowLoadChapterContentFailedToast : ReaderEffect()
}