package io.github.lycosmic.lithe.presentation.settings.browse

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.lycosmic.lithe.data.DisplayMode
import io.github.lycosmic.lithe.data.local.DirectoryDao
import io.github.lycosmic.lithe.data.local.ScannedDirectory
import io.github.lycosmic.lithe.data.settings.SettingsManager
import io.github.lycosmic.lithe.utils.UriUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrowseSettingsViewModel @Inject constructor(
    private val directoryDao: DirectoryDao,
    private val application: Application,
    private val settingsManager: SettingsManager
) : ViewModel() {

    // 已授权的文件夹列表
    val scannedDirectories = directoryDao.getScannedDirectories().stateIn( // 转为热流
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
            val resolver = application.contentResolver

            try {
                // 获取文件夹的持久化读写权限
                resolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            } catch (e: Exception) {
                e.printStackTrace()
                return@launch
            }

            // 获取文件的路径信息
            val (rootName, relativePath) = UriUtils.parseUriToPath(uri)

            val scannedDirectory = ScannedDirectory(
                uri = uri.toString(),
                root = rootName,
                path = relativePath
            )

            directoryDao.insertDirectory(scannedDirectory)
        }
    }

    /**
     * 移除文件夹
     */
    fun removeDirectory(directory: ScannedDirectory) {
        viewModelScope.launch(Dispatchers.IO) {
            directoryDao.deleteDirectory(directory)
        }
    }
}