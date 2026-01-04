package io.github.lycosmic.lithe.presentation.reader.components

sealed class ReaderEvent {
    // --- 顶部区域 ---
    /**
     * 点击返回按钮
     */
    object OnBackClick : ReaderEvent()

    // --- 中间区域 ---
    /**
     * 点击阅读内容
     */
    object OnContentClick : ReaderEvent()

    // --- 底部区域 ---
}