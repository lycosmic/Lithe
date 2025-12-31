package io.github.lycosmic.lithe.presentation.reader.components

sealed class ReaderEvent {
    /**
     * 点击阅读内容
     */
    object OnContentClick : ReaderEvent()
}