package io.github.lycosmic.lithe.presentation.settings.browse

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.lycosmic.lithe.data.repository.DirectoryRepositoryImpl
import io.github.lycosmic.lithe.data.settings.SettingsManager
import io.github.lycosmic.lithe.log.logV
import io.github.lycosmic.lithe.util.UiConfig
import io.github.lycosmic.model.Directory
import io.github.lycosmic.model.DisplayMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrowseSettingsViewModel @Inject constructor(
    private val directoryRepositoryImpl: DirectoryRepositoryImpl,
    private val settingsManager: SettingsManager
) : ViewModel() {

    // 已授权的文件夹列表
    val scannedDirectories = directoryRepositoryImpl.getDirectoriesFlow().stateIn( // 转为热流
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(UiConfig.STATE_FLOW_STOP_TIMEOUT),
        initialValue = emptyList()
    )

    // 当前的文件夹显示模式
    val displayMode = settingsManager.fileDisplayMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(UiConfig.STATE_FLOW_STOP_TIMEOUT),
        initialValue = DisplayMode.List
    )

    // 当前的网格列数
    val gridColumnCount = settingsManager.fileGridColumnCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(UiConfig.STATE_FLOW_STOP_TIMEOUT),
        initialValue = SettingsManager.GRID_COLUMN_COUNT_DEFAULT
    )


    private val _effects = MutableSharedFlow<BrowseSettingEffect>()
    val effects = _effects.asSharedFlow()

    fun onEvent(event: BrowseSettingEvent) {
        viewModelScope.launch {
            when (event) {
                is BrowseSettingEvent.AddDirectoryClicked -> {
                    _effects.emit(BrowseSettingEffect.OpenDirectoryPicker)
                }

                is BrowseSettingEvent.DeleteDirectoryClicked -> {
                    removeDirectory(event.directory)
                }

                is BrowseSettingEvent.DisplayModeChanged -> {
                    onDisplayModeChanged(event.newMode)
                }

                is BrowseSettingEvent.GridColumnCountChanged -> {
                    onGridColumnCountChanged(event.newCount)
                }

                is BrowseSettingEvent.DirectoryPicked -> {
                    addDirectory(event.uri)
                }
            }
        }

    }

    /**
     * 修改文件显示模式
     */
    fun onDisplayModeChanged(newMode: DisplayMode) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsManager.setFileDisplayMode(newMode)
        }
    }

    /**
     * 添加文件夹
     */
    fun addDirectory(uri: Uri) {
        viewModelScope.launch(Dispatchers.IO) {
            directoryRepositoryImpl.insertDirectory(uri.toString())
        }
    }

    /**
     * 移除文件夹
     */
    fun removeDirectory(directory: Directory) {
        viewModelScope.launch(Dispatchers.IO) {
            directoryRepositoryImpl.removeDirectory(directory)
        }
    }

    /**
     * 修改网格列数
     */
    fun onGridColumnCountChanged(newCount: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            logV {
                "Modify the number of grid columns: $newCount"
            }
            settingsManager.setFileGridColumnCount(newCount)

            logV {
                "Modify the number of grid columns completed"
            }
        }
    }
}