package io.github.lycosmic.lithe.presentation.browse

sealed class BrowseEffect {
    /**
     * 显示成功导入所有书籍的 Toast
     */
    object ShowSuccessToast : BrowseEffect()

    /**
     * 显示导入成功的书籍数量的 Toast
     */
    data class ShowSuccessCountToast(val count: Long) : BrowseEffect()

}