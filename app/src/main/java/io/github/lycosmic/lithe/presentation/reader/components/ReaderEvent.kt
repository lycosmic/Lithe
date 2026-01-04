package io.github.lycosmic.lithe.presentation.reader.components

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

    // --- 底部区域 ---


    // --- 章节列表区域 ---
    /**
     * 点击抽屉返回按钮
     */
    object OnDrawerBackClick : ReaderEvent()
}