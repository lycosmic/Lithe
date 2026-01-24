package io.github.lycosmic.lithe.presentation.reader

sealed class ReaderEvent {
    // --- 顶部区域 ---
    /**
     * 点击返回按钮
     */
    object OnBackClick : ReaderEvent()

    /**
     * 点击章节菜单按钮
     */
    object OnChapterMenuClick : ReaderEvent()

    // --- 中间区域 ---
    /**
     * 点击阅读内容
     */
    object OnReadContentClick : ReaderEvent()

    /**
     * 滚动位置改变
     */
    data class OnScrollPositionChanged(val visibleContent: ReaderContent) : ReaderEvent()


    /**
     * 用户离开页面，立即保存当前内存中的进度到数据库
     */
    data class OnStopOrDispose(val currentContent: ReaderContent?) : ReaderEvent()


    // --- 底部区域 ---
    /**
     * 点击上一章按钮
     */
    data object OnPrevChapterClick : ReaderEvent()

    /**
     * 点击下一章按钮
     */
    data object OnNextChapterClick : ReaderEvent()


    // --- 章节列表区域 ---
    /**
     * 点击抽屉返回按钮
     */
    object OnDrawerBackClick : ReaderEvent()

    /**
     * 点击章节列表项
     */
    data class OnChapterItemClick(val index: Int) : ReaderEvent()
}