package io.github.lycosmic.data.settings


/**
 * 阅读器翻页动画模式，阅读模式
 */
enum class AppPageAnim {
    SIMULATION, // 曲面翻页效果
    SLIDE,      // 平移切换
    SCROLL,     // 上下连续滚动
    NONE        // 直接切换，无动画
}