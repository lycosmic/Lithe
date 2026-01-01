package io.github.lycosmic.lithe.presentation.browse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.lycosmic.lithe.data.repository.BookRepositoryImpl
import io.github.lycosmic.lithe.data.settings.SettingsManager
import io.github.lycosmic.lithe.domain.model.Constants
import io.github.lycosmic.lithe.domain.model.DisplayMode
import io.github.lycosmic.lithe.domain.model.FileFormat
import io.github.lycosmic.lithe.domain.model.FileItem
import io.github.lycosmic.lithe.domain.model.FilterOption
import io.github.lycosmic.lithe.domain.model.SortType
import io.github.lycosmic.lithe.domain.model.SortType.Companion.DEFAULT_IS_ASCENDING
import io.github.lycosmic.lithe.domain.model.SortType.Companion.DEFAULT_SORT_TYPE
import io.github.lycosmic.lithe.domain.use_case.BookImportUseCase
import io.github.lycosmic.lithe.domain.use_case.FileProcessingUseCase
import io.github.lycosmic.lithe.presentation.browse.model.BrowseTopBarState
import io.github.lycosmic.lithe.presentation.browse.model.FileFilterState
import io.github.lycosmic.lithe.presentation.browse.model.ParsedBook
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class BrowseViewModel @Inject constructor(
    private val fileProcessingUseCase: FileProcessingUseCase,
    private val bookImportUseCase: BookImportUseCase,
    bookRepositoryImpl: BookRepositoryImpl,
    private val settingsManager: SettingsManager
) : ViewModel() {

    // 原始的文件列表
    private val _rawFileItems = MutableStateFlow<List<FileItem>>(emptyList())

    // 是否正在加载
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // 当前选中的文件, 根据 Uri 判断是否为选中
    private val _selectedFileUris = MutableStateFlow<Set<String>>(emptySet())
    val selectedFiles = _selectedFileUris.asStateFlow()

    // 带有元数据的书籍
    private val _parsedBooks = MutableStateFlow<List<ParsedBook>>(emptyList())
    val parsedBooks = _parsedBooks.asStateFlow()

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

    // 当前是否为搜索模式
    private val _isSearchMode = MutableStateFlow(false)
    val isSearchMode = _isSearchMode.asStateFlow()

    // 当前是否为多选模式
    val isMultiSelectMode = _selectedFileUris.asStateFlow().map {
        it.isNotEmpty()
    }

    // 搜索的文本内容
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    // 在数据库中的书籍文件Uri列表
    private val _dbFileUris = bookRepositoryImpl.getAllBooks().map {
        return@map it.map { book ->
            book.fileUri
        }.toSet()
    }

    // 文件显示模式
    val fileDisplayMode = settingsManager.fileDisplayMode.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(Constants.STATE_FLOW_STOP_TIMEOUT_MILLIS),
        DisplayMode.List
    )

    // 网格列数
    val gridColumnCount = settingsManager.fileGridColumnCount.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(Constants.STATE_FLOW_STOP_TIMEOUT_MILLIS),
        SettingsManager.GRID_COLUMN_COUNT_DEFAULT
    )

    // 控制文件夹头部是否固定的状态
    private val _pinnedHeaders = MutableStateFlow<Set<String>>(emptySet())
    val pinnedHeaders: StateFlow<Set<String>> = _pinnedHeaders.asStateFlow()

    private val _fileFilterState = combine(
        _sortType,
        _isAscending,
        _filterList,
        _searchText,
        _dbFileUris
    ) { sort, isAscending, filters, searchText, dbFileUris ->
        FileFilterState(
            sortType = sort,
            isAscending = isAscending,
            filterOptions = filters,
            searchText = searchText,
            bookFileUris = dbFileUris
        )
    }

    // 分组、排序、文件类型过滤、关键词过滤后的文件列表
    val processedFiles = combine(
        _rawFileItems,
        _fileFilterState,
    ) { files, fileFilterState ->
        fileProcessingUseCase.processFiles(
            rawFiles = files,
            sortType = fileFilterState.sortType,
            isAscending = fileFilterState.isAscending,
            filters = fileFilterState.filterOptions,
            searchText = fileFilterState.searchText,
            dbFileUris = fileFilterState.bookFileUris
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(Constants.STATE_FLOW_STOP_TIMEOUT_MILLIS),
        emptyMap()
    )

    // 顶部栏状态
    val topBarState = combine(
        _selectedFileUris,
        _isSearchMode,
    ) { fileUris, isSearchMode ->
        // 优先级: 多选模式 > 搜索模式 > 默认模式
        if (fileUris.isNotEmpty()) {
            return@combine BrowseTopBarState.SELECT_FILE
        } else if (isSearchMode) {
            return@combine BrowseTopBarState.SEARCH
        }

        return@combine BrowseTopBarState.DEFAULT
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(Constants.STATE_FLOW_STOP_TIMEOUT_MILLIS),
        BrowseTopBarState.DEFAULT
    )

    private val _effects = MutableSharedFlow<BrowseEffect>()
    val effects = _effects.asSharedFlow()


    init {
        observeDirectoriesAndScan()
    }

    fun onEvent(event: BrowseEvent) {
        viewModelScope.launch {
            when (event) {
                BrowseEvent.OnAddFileClick -> {
                    parseSelectedFilesMetaData()
                }

                BrowseEvent.OnImportBooksClick -> {
                    importFinalSelectedBooks()
                }

                BrowseEvent.OnClearSelectionClick -> {
                    // 取消选中
                    clearSelection()
                }

                BrowseEvent.OnToggleAllSelectionClick -> {
                    // 全选
                    toggleAllSelection()
                }

                BrowseEvent.OnBackClick -> {
                    handleBackPress()
                }

                is BrowseEvent.OnFileCheckboxClick -> {
                    // 切换单个文件的选中状态
                    toggleSelection(event.file)
                }

                is BrowseEvent.OnFileClick -> {
                    // 切换单个文件的选中状态
                    toggleSelection(event.file)
                }

                // 文件长按
                is BrowseEvent.OnFileLongClick -> {
                    handleFileLongPress(event.files)
                }

                // 离开添加书籍的对话框
                BrowseEvent.OnDismissAddBooksDialog -> {
                    clearSelection()
                }

                is BrowseEvent.OnBookItemClick -> {
                    toggleBookSelection(event.bookItem)
                }

                is BrowseEvent.OnSortTypeChange -> {
                    updateSort(event.sortType, event.isAscending)
                }

                is BrowseEvent.OnFilterChange -> {
                    // 点击的文件类型
                    val clickedFileFormat = event.filterOption.format
                    updateFileTypeFilter(clickedFileFormat)
                }

                is BrowseEvent.OnSearchClick -> {
                    _isSearchMode.value = true
                }

                is BrowseEvent.OnExitSearchClick -> {
                    _isSearchMode.value = false
                    _searchText.value = ""
                }

                is BrowseEvent.OnSearchTextChange -> {
                    _searchText.value = event.text
                }

                is BrowseEvent.OnDisplayModeChange -> {
                    settingsManager.setFileDisplayMode(event.newDisplayMode)
                }

                is BrowseEvent.OnGridSizeChange -> {
                    settingsManager.setFileGridColumnCount(event.newGridSize)
                }

                is BrowseEvent.OnPinHeaderClick -> {
                    toggleHeaderPin(event.path)
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
            fileProcessingUseCase.scanFiles().collectLatest { items ->
                _rawFileItems.value = items
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
     * 处理按下返回键
     */
    fun handleBackPress() {
        if (topBarState.value == BrowseTopBarState.SELECT_FILE) {
            // 如果当前为多选模式，就清除选中项
            clearSelection()
        } else if (topBarState.value == BrowseTopBarState.SEARCH) {
            // 如果为搜索模式，就取消搜索
            _isSearchMode.value = false
        }
    }

    /**
     * 取消全选
     * 清空选中状态: 包括选中的文件 Uri 和待导入的书籍
     */
    fun clearSelection() {
        _selectedFileUris.value = emptySet()
        _parsedBooks.value = emptyList()
    }

    /**
     * 解析所选文件的元数据
     */
    fun parseSelectedFilesMetaData() {
        viewModelScope.launch(Dispatchers.IO) {
            _isAddBooksDialogLoading.value = true
            // 获取选中的文件
            val selectedFiles = getSelectedFiles()
            // 解析文件元数据
            _parsedBooks.value = bookImportUseCase.parseFileMetadata(selectedFiles)
            _isAddBooksDialogLoading.value = false
        }
    }

    /**
     * 导入最终选中的书籍
     */
    fun importFinalSelectedBooks() {
        viewModelScope.launch(Dispatchers.IO) {

            // 获取用户最终勾选的文件
            val parsedBooks = _parsedBooks.value.filter {
                it.isSelected
            }

            val success = bookImportUseCase.importBook(parsedBooks)

            if (success.toInt() == parsedBooks.size) {
                _effects.emit(BrowseEffect.ShowSuccessToast)
            } else {
                _effects.emit(BrowseEffect.ShowSuccessCountToast(success))
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
     * 更新文件类型过滤器
     */
    fun updateFileTypeFilter(fileFormat: FileFormat) {
        // 更新选中状态
        _filterList.value = _filterList.value.map {
            if (it.format == fileFormat) {
                it.copy(isSelected = !it.isSelected)
            } else {
                it
            }
        }
    }

    /**
     * 处理文件长按
     */
    suspend fun handleFileLongPress(files: List<FileItem>) {
        if (!isMultiSelectMode.first()) {
            // 选中这组文件
            files.forEach { fileItem ->
                toggleSelection(fileItem)
            }
        }
    }

    fun toggleBookSelection(bookItem: ParsedBook) {
        // 修改选中项的选中状态
        val newList = _parsedBooks.value.map { bookToAdd ->
            if (bookToAdd.file.uri == bookItem.file.uri) {
                // 修改选中状态
                bookToAdd.copy(isSelected = !bookToAdd.isSelected)
            } else {
                // 其他项不变
                bookToAdd
            }
        }

        _parsedBooks.value = newList
    }

    /**
     * 切换头部固定状态
     */
    fun toggleHeaderPin(path: String) {
        _pinnedHeaders.update { currentPins ->
            if (currentPins.contains(path)) {
                // 如果已固定，则取消固定
                currentPins - path
            } else {
                // 如果未固定，则固定
                currentPins + path
            }
        }
    }
}