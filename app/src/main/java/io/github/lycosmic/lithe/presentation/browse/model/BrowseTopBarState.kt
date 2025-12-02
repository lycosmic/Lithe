package io.github.lycosmic.lithe.presentation.browse.model


/**
 * 浏览页面顶部栏状态
 */
enum class BrowseTopBarState {
    // 默认状态
    DEFAULT,

    // 多选模式 - 文件选择状态
    SELECT_FILE,

    // 搜索模式 - 搜索状态
    SEARCH,
}