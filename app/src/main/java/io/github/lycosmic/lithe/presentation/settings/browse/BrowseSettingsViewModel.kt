package io.github.lycosmic.lithe.presentation.settings.browse

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.lycosmic.lithe.data.model.Constants
import io.github.lycosmic.lithe.data.model.DisplayMode
import io.github.lycosmic.lithe.data.model.ScannedDirectory
import io.github.lycosmic.lithe.data.repository.DirectoryRepository
import io.github.lycosmic.lithe.data.settings.SettingsManager
import io.github.lycosmic.lithe.extension.logV
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
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
        started = SharingStarted.WhileSubscribed(Constants.STATE_FLOW_STOP_TIMEOUT_MILLIS),
        initialValue = emptyList()
    )

    // 当前的文件夹显示模式
    val displayMode = settingsManager.fileDisplayMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(Constants.STATE_FLOW_STOP_TIMEOUT_MILLIS),
        initialValue = DisplayMode.List
    )

    // 当前的网格列数
    val gridColumnCount = settingsManager.fileGridColumnCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(Constants.STATE_FLOW_STOP_TIMEOUT_MILLIS),
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