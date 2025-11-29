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
import io.github.lycosmic.lithe.presentation.browse.model.BookToAdd
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
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
    private val _selectedFileUris = MutableStateFlow<Set<String>>(emptySet())
    val selectedFiles = _selectedFileUris.asStateFlow()

    // 待导入的书籍
    private val _bookToImport = MutableStateFlow<List<BookToAdd>>(emptyList())
    val bookToImport = _bookToImport.asStateFlow()

    // 对话框是否在加载
    private val _isAddBooksDialogLoading = MutableStateFlow(false)
    val isAddBooksDialogLoading = _isAddBooksDialogLoading.asStateFlow()


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
            val fileItems = groupedFiles.value.map { entry ->
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

            // 从 Set 中获取选中的文件
            val selectedFiles = getSelectedFiles()

            // 解析元数据
            val parsedBooks = selectedFiles.map { file ->
                async {
                    Timber.d("Parsing metadata for file: %s", file.name)
                    val parser = parserFactory.getParser(file.type.value)
                    if (parser == null) {
                        Timber.w("No parser found for file: %s", file.name)
                        return@async null
                    }

                    val metadata = parser.parse(application, file.uri)

                    return@async BookToAdd(
                        file = file,
                        metadata = metadata
                    )
                }
            }.awaitAll()



            _bookToImport.value = parsedBooks.filterNotNull()
            _isAddBooksDialogLoading.value = false
        }
    }

    /**
     * 真正导入选中的文件
     */
    fun confirmImport() {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.i("Start importing the user's selected books")

            // 获取所有用户勾选的文件
            val bookToImprots = _bookToImport.value.filter {
                it.isSelected
            }

            Timber.d("User selected %d books", bookToImprots.size)

            bookToImprots.forEach { bookToImport ->

                val metadata = bookToImport.metadata
                val bookFile = bookToImport.file
                val uniqueId = metadata.uniqueId ?: bookFile.uri.toString()

                // 顺序排好的书签列表
                val sortedBookmarks = metadata.bookmarks?.sortedBy { it.order }

                if (bookRepository.getBookByUniqueId(uniqueId) != null) {
                    // 已有该书籍
                    Timber.w("The book is already in the database with a unique identifier of $uniqueId")
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

            Timber.i("Import books successfully")

            // 导入完成后，清空选中状态
            clearSelection()
        }
    }

    /**
     * 获取当前选中的文件
     */
    private fun getSelectedFiles(): List<FileItem> {
        val allFiles = _groupedFiles.value.values.flatten()
        // 获取选中的文件
        val selectedFiles = allFiles.filter { item ->
            _selectedFileUris.value.contains(item.uri.toString())
        }
        return selectedFiles
    }


}