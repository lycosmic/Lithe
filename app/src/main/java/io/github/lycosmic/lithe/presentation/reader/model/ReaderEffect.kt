package io.github.lycosmic.lithe.presentation.reader.model

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
}