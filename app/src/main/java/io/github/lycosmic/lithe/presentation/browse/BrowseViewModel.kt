package io.github.lycosmic.lithe.presentation.browse

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.lycosmic.lithe.data.model.Book
import io.github.lycosmic.lithe.data.model.FileItem
import io.github.lycosmic.lithe.data.parser.metadata.BookMetadataParserFactory
import io.github.lycosmic.lithe.data.repository.BookRepository
import io.github.lycosmic.lithe.data.repository.DirectoryRepository
import io.github.lycosmic.lithe.domain.model.FilterOption
import io.github.lycosmic.lithe.domain.model.SortType
import io.github.lycosmic.lithe.domain.model.SortType.Companion.DEFAULT_IS_ASCENDING
import io.github.lycosmic.lithe.domain.model.SortType.Companion.DEFAULT_SORT_TYPE
import io.github.lycosmic.lithe.extension.logD
import io.github.lycosmic.lithe.extension.logI
import io.github.lycosmic.lithe.extension.logW
import io.github.lycosmic.lithe.presentation.browse.model.BookToAdd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val application: Application,
    private val directoryRepository: DirectoryRepository,
    private val bookRepository: BookRepository,
    private val parserFactory: BookMetadataParserFactory
) : ViewModel() {

    // 原始的文件列表
    private val _rawFileItems = MutableStateFlow<List<FileItem>>(emptyList())

    // 是否正在加载
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // 当前选中的文件, 根据 Uri 判断是否为选中
    private val _selectedFileUris = MutableStateFlow<Set<String>>(emptySet())
    val selectedFiles = _selectedFileUris.asStateFlow()

    // 待导入的书籍
    private val _bookToImport = MutableStateFlow<List<BookToAdd>>(emptyList())
    val bookToImport = _bookToImport.asStateFlow()

    // 对话框是否在加载
    private val _isAddBooksDialogLoading = MutableStateFlow(false)
    val isAddBooksDialogLoading = _isAddBooksDialogLoading.asStateFlow()

    // 当前的排序
    private val _sortType = MutableStateFlow(DEFAULT_SORT_TYPE)
    val sortType = _sortType.asStateFlow()

    private val _isAscending = MutableStateFlow(DEFAULT_IS_ASCENDING)
    val isAscending = _isAscending.asStateFlow()

    // 当前的过滤列表
    private val _filterList = MutableStateFlow(FilterOption.defaultFilterOptions)
    val filterList = _filterList.asStateFlow()

    // 分组、排序、过滤后的文件列表
    val processedFiles = combine(
        _rawFileItems,
        _sortType,
        _isAscending,
        _filterList
    ) { files, sort, isAscending, filters ->
        logD { "current filters: $filters" }

        val filteredFiles = if (
            isFilterValid(filters)
        ) {
            files.filter { file ->
                logD { "Filtering file: $files" }

                val accepted = filters.any { filter ->
                    file.type == filter.format && filter.isSelected
                }
                if (!accepted) {
                    logD { "File rejected: $file" }
                } else {
                    logD { "File accepted: $file, file type: ${file.type}" }
                }
                accepted
            }
        } else {
            files
        }

        logD { "Filtered file: $filteredFiles" }

        // 排序
        val sortedFiles = sortFiles(filteredFiles, sort, isAscending)

        // 按父文件夹进行分组
        sortedFiles.groupBy { item ->
            item.parentPath
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyMap()
    )


    // 当前是否为多选模式
    val isMultiSelectMode = _selectedFileUris.asStateFlow().map {
        it.isNotEmpty()
    }


    init {
        observeDirectoriesAndScan()
    }

    fun onEvent(event: BrowseEvent) {
        viewModelScope.launch {
            when (event) {
                BrowseEvent.OnAddBooksClick -> {
                    prepareImport()
                }

                BrowseEvent.OnImportBooksClick -> { // 真正导入
                    // 导入选中的文件
                    confirmImport()
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

                BrowseEvent.OnDismissAddBooksDialog -> {
                    clearSelection()
                }

                is BrowseEvent.OnAddBooksDialogItemClick -> {
                    // 修改选中项的选中状态
                    val newList = _bookToImport.value.map { bookToAdd ->
                        if (bookToAdd.file.uri == event.item.file.uri) {
                            // 修改选中状态
                            bookToAdd.copy(isSelected = !bookToAdd.isSelected)
                        } else {
                            // 其他项不变
                            bookToAdd
                        }
                    }

                    _bookToImport.value = newList
                }

                is BrowseEvent.OnSortTypeChange -> {
                    updateSort(event.sortType, event.isAscending)
                }

                is BrowseEvent.OnFilterChange -> {
                    // 找到点击的文件扩展名
                    val clickedFileFormat = event.filterOption
                    // 更新选中状态
                    _filterList.value = _filterList.value.map {
                        if (it.format == clickedFileFormat.format) {
                            it.copy(isSelected = !it.isSelected)
                        } else {
                            it
                        }
                    }
                }
            }
        }

    }

    /**
     * 更新类型和顺序
     */
    fun updateSort(type: SortType, ascending: Boolean) {
        _sortType.value = type
        _isAscending.value = ascending
    }

    /**
     * 监听文件夹列表,每当文件夹列表发生变化时,扫描所有文件
     */
    fun observeDirectoriesAndScan() {
        viewModelScope.launch(Dispatchers.IO) {
            // 监听文件夹列表变化
            directoryRepository.getDirectories().collectLatest { directories ->
                _isLoading.value = true

                // 扫描所有文件
                val fileItems = directoryRepository.scanAllBooks(directories)
                _rawFileItems.value = fileItems

                _isLoading.value = false
            }
        }
    }


    /**
     * 切换单个文件的选中状态
     */
    fun toggleSelection(file: FileItem) {
        val currentSet = _selectedFileUris.value.toMutableSet()

        val key = file.uri.toString()
        val exists = currentSet.contains(key)
        if (exists) {
            currentSet.remove(key)
        } else {
            currentSet.add(key)
        }

        // 更新选中的文件
        _selectedFileUris.value = currentSet
    }


    /**
     * 全选
     */
    fun toggleAllSelection() {
        viewModelScope.launch(Dispatchers.IO) {
            val fileItems = processedFiles.value.map { entry ->
                entry.value.map { item ->
                    item.uri.toString()
                }
            }.flatten().toSet()
            _selectedFileUris.value = fileItems
        }
    }

    /**
     * 取消全选
     * 清空选中状态: 包括选中的文件 Uri 和待导入的书籍
     */
    fun clearSelection() {
        _selectedFileUris.value = emptySet()
        _bookToImport.value = emptyList()
    }

    /**
     * 解析元数据
     */
    fun prepareImport() {
        viewModelScope.launch(Dispatchers.IO) {
            _isAddBooksDialogLoading.value = true

            logI {
                "Start parsing book metadata"
            }

            // 从 Set 中获取选中的文件
            val selectedFiles = getSelectedFiles()

            // 解析元数据
            val parsedBooks = selectedFiles.map { file ->
                async {
                    logD {
                        "Parsing metadata for file: ${file.name}"
                    }
                    val parser = parserFactory.getParser(file.type.value)
                    if (parser == null) {
                        logW {
                            "No parser found for file: ${file.name}"
                        }
                        return@async null
                    }

                    val metadata = parser.parse(application, file.uri)

                    logD {
                        "Parsed metadata for file: ${file.name}, metadata: $metadata"
                    }

                    return@async BookToAdd(
                        file = file,
                        metadata = metadata
                    )
                }
            }.awaitAll()


            _bookToImport.value = parsedBooks.filterNotNull()

            logI {
                "Finished parsing book metadata"
            }
            _isAddBooksDialogLoading.value = false
        }
    }

    /**
     * 真正导入选中的文件
     */
    fun confirmImport() {
        viewModelScope.launch(Dispatchers.IO) {
            logI {
                "Start importing the user's selected books"
            }

            // 获取所有用户勾选的文件
            val bookToImprots = _bookToImport.value.filter {
                it.isSelected
            }

            logD {
                "User selected ${bookToImprots.size} books"
            }

            bookToImprots.forEach { bookToImport ->
                logD {
                    "Books to be imported: ${bookToImport.file.name}, metadata: ${bookToImport.metadata}, file item: ${bookToImport.file}"
                }
                val metadata = bookToImport.metadata
                val bookFile = bookToImport.file
                val uniqueId = metadata.uniqueId ?: bookFile.uri.toString()

                // 顺序排好的书签列表
                val sortedBookmarks = metadata.bookmarks?.sortedBy { it.order }

                if (bookRepository.getBookByUniqueId(uniqueId) != null) {
                    // 已有该书籍
                    logW {
                        "The book is already in the database with a unique identifier of $uniqueId"
                    }
                    return@forEach
                }

                // 导入数据库
                val book = Book(
                    title = metadata.title ?: "未知",
                    author = metadata.authors?.firstOrNull() ?: "未知",
                    description = metadata.description,
                    coverPath = metadata.coverPath,
                    fileSize = bookFile.size,
                    fileUri = bookFile.uri.toString(),
                    format = bookFile.type.name.lowercase(),
                    importTime = System.currentTimeMillis(),
                    uniqueId = uniqueId,
                    language = metadata.language ?: "zh-CN",
                    publisher = metadata.publisher ?: "未知",
                    subjects = metadata.subjects ?: emptyList(),
                    bookmarks = sortedBookmarks,
                    lastReadPosition = null,
                    lastReadTime = null
                )
                bookRepository.importBook(book)
            }

            logI {
                "Import books successfully"
            }

            // 导入完成后，清空选中状态
            clearSelection()
        }
    }

    /**
     * 获取当前选中的文件
     */
    private fun getSelectedFiles(): List<FileItem> {
        // 获取选中的文件
        val selectedFiles = _rawFileItems.value.filter { fileItem ->
            _selectedFileUris.value.contains(fileItem.uri.toString())
        }
        return selectedFiles
    }

    /**
     * 排序文件列表
     */
    private fun sortFiles(
        files: List<FileItem>,
        type: SortType,
        ascending: Boolean
    ): List<FileItem> {
        if (files.isEmpty()) return emptyList()

        val comparator = when (type) {
            SortType.ALPHABETICAL -> compareBy<FileItem> { it.name }
            SortType.SIZE -> compareBy { it.size }
            SortType.LAST_MODIFIED -> compareBy { it.lastModified }
            SortType.FILE_FORMAT -> compareBy { it.type.value }
        }

        return if (ascending) {
            files.sortedWith(comparator)
        } else {
            files.sortedWith(comparator).reversed()
        }
    }


    /**
     * 判断当前过滤列表是否有效
     */
    private fun isFilterValid(filters: List<FilterOption>): Boolean {
        filters.forEach { filter ->
            if (filter.isSelected) {
                return true
            }
        }
        return false
    }

}