package io.github.lycosmic.lithe.presentation.browse

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.lycosmic.lithe.data.model.Book
import io.github.lycosmic.lithe.data.model.FileItem
import io.github.lycosmic.lithe.data.parser.BookMetadataParserFactory
import io.github.lycosmic.lithe.data.repository.BookRepository
import io.github.lycosmic.lithe.data.repository.DirectoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val application: Application,
    private val directoryRepository: DirectoryRepository,
    private val bookRepository: BookRepository,
    private val parserFactory: BookMetadataParserFactory
) : ViewModel() {
    // 分组后的文件列表
    private val _groupedFiles = MutableStateFlow<Map<String, List<FileItem>>>(emptyMap())
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

    fun onEvent(event: BrowseEvent) {
        viewModelScope.launch {
            when (event) {
                BrowseEvent.OnImportBooksClick -> {
                    // 导入选中的文件
                    importSelectedBooks()
                }

                BrowseEvent.OnCancelClick -> {
                    // 取消选中
                    clearSelection()
                }

                BrowseEvent.OnToggleAllSelectionClick -> {
                    // 全选
                    toggleAllSelection()
                }

                BrowseEvent.OnBackClick -> {
                    // 取消选中
                    clearSelection()
                }

                is BrowseEvent.OnFileCheckboxClick -> {
                    // 切换单个文件的选中状态
                    toggleSelection(event.file)
                }

                is BrowseEvent.OnFileClick -> {
                    // 切换单个文件的选中状态
                    toggleSelection(event.file)
                }

                is BrowseEvent.OnFileLongClick -> {
                    // 选中这组文件
                    if (!isMultiSelectMode.first()) {
                        event.files.forEach { fileItem ->
                            toggleSelection(fileItem)
                        }
                    }
                }
            }
        }

    }

    /**
     * 监听文件夹列表,每当文件夹列表发生变化时,扫描所有文件
     */
    fun observeDirectoriesAndScan() {
        viewModelScope.launch(Dispatchers.IO) {
            // 监听文件夹列表变化
            directoryRepository.getDirectories().collectLatest {
                _isLoading.value = true

                val fileItems = directoryRepository.scanAllBooks()
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
    fun toggleSelection(file: FileItem) {
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
                // 解析元数据
                val parser = parserFactory.getParser(item.type)
                val metadata = parser.parse(application, item.uri)

                // 导入数据库
                val book = Book(
                    title = metadata.title,
                    author = metadata.author,
                    description = metadata.description,
                    coverPath = metadata.coverPath,
                    fileSize = item.size,
                    fileUri = item.uri.toString(),
                    format = item.type.name.lowercase(),
                    importTime = System.currentTimeMillis(),
                )
                bookRepository.importBook(book)
            }

            // 导入完成后，清空选中状态
            clearSelection()
        }
    }


}