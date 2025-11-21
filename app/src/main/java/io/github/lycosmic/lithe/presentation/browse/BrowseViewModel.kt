package io.github.lycosmic.lithe.presentation.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.lycosmic.lithe.data.model.SelectableFileItem
import io.github.lycosmic.lithe.data.repository.DirectoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val repository: DirectoryRepository
) : ViewModel() {
    private val _groupedFiles = MutableStateFlow<Map<String, List<SelectableFileItem>>>(emptyMap())
    val groupedFiles = _groupedFiles.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        refreshFiles()
    }

    fun refreshFiles() {
        viewModelScope.launch(Dispatchers.IO) {
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