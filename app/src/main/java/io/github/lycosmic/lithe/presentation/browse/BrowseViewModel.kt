package io.github.lycosmic.lithe.presentation.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.lycosmic.lithe.data.model.SelectableFileItem
import io.github.lycosmic.lithe.data.repository.DirectoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val repository: DirectoryRepository
) : ViewModel() {
    // 分组后的文件列表
    private val _groupedFiles = MutableStateFlow<Map<String, List<SelectableFileItem>>>(emptyMap())
    val groupedFiles = _groupedFiles.asStateFlow()

    // 是否正在加载
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // 当前选中的文件, 根据 Uri 判断是否为选中
    private val _selectedFiles = MutableStateFlow<Set<String>>(emptySet())
    val selectedFiles = _selectedFiles.asStateFlow()

    // 当前是否为多选模式
    val isMultiSelectMode = _selectedFiles.asStateFlow().map {
        it.isNotEmpty()
    }


    init {
        observeDirectoriesAndScan()
    }

    /**
     * 监听文件夹列表,每当文件夹列表发生变化时,扫描所有文件
     */
    fun observeDirectoriesAndScan() {
        viewModelScope.launch(Dispatchers.IO) {
            // 监听文件夹列表变化
            repository.getDirectories().collectLatest {
                _isLoading.value = true

                val fileItems = repository.scanAllBooks()
                // 按父文件夹进行分组
                val groupByParentPath = fileItems.groupBy { item ->
                    item.parentPath
                }

                _groupedFiles.value = groupByParentPath
                _isLoading.value = false
            }
        }
    }


    /**
     * 切换单个文件的选中状态
     */
    fun toggleSelection(file: SelectableFileItem) {
        val currentSet = _selectedFiles.value.toMutableSet()

        val key = file.uri.toString()
        val exists = currentSet.contains(key)
        if (exists) {
            currentSet.remove(key)
        } else {
            currentSet.add(key)
        }

        // 更新选中的文件
        _selectedFiles.value = currentSet
    }


    /**
     * 全选
     */
    fun toggleAllSelection() {
        viewModelScope.launch(Dispatchers.IO) {
            val fileItems = groupedFiles.value.map { entry ->
                entry.value.map { item ->
                    item.uri.toString()
                }
            }.flatten().toSet()
            _selectedFiles.value = fileItems
        }
    }

    /**
     * 取消全选
     */
    fun clearSelection() {
        _selectedFiles.value = emptySet()
    }

    /**
     * 导入选中的文件
     */
    fun importSelectedBooks() {
        viewModelScope.launch(Dispatchers.IO) {
            val allFiles = _groupedFiles.value.values.flatten()
            // 获取选中的文件
            val selectedFiles = allFiles.filter { item ->
                _selectedFiles.value.contains(item.uri.toString())
            }

            selectedFiles.forEach { item ->
                // TODO 导入书籍
            }

            // 导入完成后，清空选中状态
            clearSelection()
        }
    }


}