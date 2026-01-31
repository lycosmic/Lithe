package io.github.lycosmic.lithe.util

/**
 * 获取IntRange的总长度（包含元素的个数）
 */
val IntRange.length: Int
    get() = if (isEmpty()) 0 else endInclusive - start + 1