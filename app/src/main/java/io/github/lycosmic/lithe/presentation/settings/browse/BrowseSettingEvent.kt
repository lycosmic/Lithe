package io.github.lycosmic.lithe.presentation.settings.browse

import android.net.Uri
import io.github.lycosmic.model.Directory
import io.github.lycosmic.model.DisplayMode


sealed class BrowseSettingEvent {
    /**
     * 添加文件夹被点击
     */
    object AddDirectoryClicked : BrowseSettingEvent()

    /**
     * 删除文件夹被点击
     */
    data class DeleteDirectoryClicked(val directory: Directory) : BrowseSettingEvent()

    /**
     * 文件显示模式被改变
     */
    data class DisplayModeChanged(val newMode: DisplayMode) : BrowseSettingEvent()

    /**
     * 网格列数被改变
     */
    data class GridColumnCountChanged(val newCount: Int) : BrowseSettingEvent()

    /**
     * 文件夹被添加
     */
    data class DirectoryPicked(val uri: Uri) : BrowseSettingEvent()
}