package io.github.lycosmic.lithe.presentation.reader.components

sealed class ReaderEffect {
    // --- 上下栏 ---
    /**
     * 显示或隐藏顶部工具栏和底部控制层
     */
    object ShowOrHideTopBarAndBottomControl : ReaderEffect()
}