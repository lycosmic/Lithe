package io.github.lycosmic.lithe.presentation.settings.browse

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.lycosmic.lithe.data.DisplayMode
import io.github.lycosmic.lithe.data.local.ScannedDirectory
import io.github.lycosmic.lithe.data.repository.DirectoryRepository
import io.github.lycosmic.lithe.data.settings.SettingsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrowseSettingsViewModel @Inject constructor(
    private val directoryRepository: DirectoryRepository,
    private val settingsManager: SettingsManager
) : ViewModel() {

    // 已授权的文件夹列表
    val scannedDirectories = directoryRepository.getDirectories().stateIn( // 转为热流
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = emptyList()
    )

    // 当前的文件夹显示模式
    val displayMode = settingsManager.fileDisplayMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = DisplayMode.List
    )

    fun onDisplayModeChanged(newMode: DisplayMode) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsManager.setFileDisplayMode(newMode)
        }
    }

    // 添加文件夹
    fun addDirectory(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            directoryRepository.addDirectory(uri)
        }
    }

    /**
     * 移除文件夹
     */
    fun removeDirectory(directory: ScannedDirectory) {
        viewModelScope.launch(Dispatchers.IO) {
            directoryRepository.removeDirectory(directory)
        }
    }
}