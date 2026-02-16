package io.github.lycosmic.lithe.presentation.history

import io.github.lycosmic.domain.model.ReadHistory


sealed class HistoryEvent {

    /**
     * 点击搜索按钮
     */
    data object OnSearchClick : HistoryEvent()

    /**
     * 退出搜索模式
     */
    data object OnExitSearchModeClick : HistoryEvent()

    /**
     * 点击阅读历史标题
     */
    data class OnHistoryTitleClick(val bookId: Long) : HistoryEvent()

    /**
     * 点击阅读历史
     */
    data class OnHistoryClick(val bookId: Long) : HistoryEvent()


    /**
     * 搜索框文本改变
     */
    data class OnSearchTextChange(val text: String) : HistoryEvent()

    /**
     * 点击删除阅读历史按钮
     */
    data class OnDeleteHistoryClick(val history: ReadHistory) : HistoryEvent()


    // --- 对话框 ---
    /**
     * 点击删除全部历史记录按钮
     */
    data object OnDeleteWholeHistoryClick : HistoryEvent()

    /**
     * 删除全部历史记录对话框消失
     */
    data object OnDeleteWholeHistoryDialogDismiss : HistoryEvent()

    /**
     * 删除全部历史记录对话框确认
     */
    data object OnDeleteWholeHistoryDialogConfirm : HistoryEvent()

    /**
     * 点击更多按钮
     */
    data object OnMoreClick : HistoryEvent()


    /**
     * 更多菜单弹窗消失
     */
    data object OnMoreBottomSheetDismiss : HistoryEvent()


    /**
     * 点击关于按钮
     */
    data object OnAboutClick : HistoryEvent()

    /**
     * 点击帮助按钮
     */
    data object OnHelpClick : HistoryEvent()

    /**
     * 点击设置按钮
     */
    data object OnSettingsClick : HistoryEvent()
}