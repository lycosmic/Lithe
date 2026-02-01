package io.github.lycosmic.lithe.presentation.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.lycosmic.data.settings.BookSortType
import io.github.lycosmic.data.settings.BookTitlePosition
import io.github.lycosmic.data.settings.DisplayMode
import io.github.lycosmic.data.settings.SettingsManager
import io.github.lycosmic.domain.model.Book
import io.github.lycosmic.domain.model.Category
import io.github.lycosmic.domain.model.CategoryWithBookList
import io.github.lycosmic.domain.repository.BookRepository
import io.github.lycosmic.domain.use_case.library.DeleteBookUseCase
import io.github.lycosmic.lithe.log.logE
import io.github.lycosmic.lithe.log.logW
import io.github.lycosmic.lithe.presentation.library.LibraryEffect.CloseDeleteBookConfirmDialog
import io.github.lycosmic.lithe.presentation.library.LibraryEffect.CloseFilterBottomSheet
import io.github.lycosmic.lithe.presentation.library.LibraryEffect.OnNavigateToAbout
import io.github.lycosmic.lithe.presentation.library.LibraryEffect.OnNavigateToBookDetail
import io.github.lycosmic.lithe.presentation.library.LibraryEffect.OnNavigateToBrowser
import io.github.lycosmic.lithe.presentation.library.LibraryEffect.OnNavigateToHelp
import io.github.lycosmic.lithe.presentation.library.LibraryEffect.OnNavigateToSettings
import io.github.lycosmic.lithe.presentation.library.LibraryEffect.OpenFilterBottomSheet
import io.github.lycosmic.lithe.presentation.library.LibraryEffect.OpenMoreOptionsBottomSheet
import io.github.lycosmic.lithe.presentation.library.LibraryEffect.ShowDeleteBookConfirmDialog
import io.github.lycosmic.lithe.presentation.library.LibraryEffect.ShowDeleteBookCountToast
import io.github.lycosmic.lithe.presentation.library.LibraryEffect.ShowDeleteBookSuccessToast
import io.github.lycosmic.lithe.presentation.library.model.BookFilterState
import io.github.lycosmic.lithe.presentation.library.model.LibraryTopBarState
import io.github.lycosmic.lithe.util.AppConstants
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val bookRepositoryImpl: BookRepository,
    private val deleteBook: DeleteBookUseCase,
    private val settingsManager: SettingsManager
) : ViewModel() {

    // 原始的书籍列表
    private val _rawBooks = bookRepositoryImpl.getAllBooks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = emptyList()
    )
    val rawBooks = _rawBooks

    // 分类以及其书籍列表
    private val _categoryWithBooksList = bookRepositoryImpl.getCategoryWithBooksFlow()

    // 总书籍数
    val totalBooksCount = _rawBooks.map { it.size }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = 0
    )


    // 当前选中的书籍, 存储书籍ID
    private val _selectedBooks = MutableStateFlow<Set<Long>>(emptySet())
    val selectedBooks = _selectedBooks.asStateFlow()


    // 当前是否为选中模式
    val isSelectionMode = _selectedBooks.map { it.isNotEmpty() }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = false
    )

    /**
     * 当前是否处于搜索模式
     */
    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    /**
     * 排序
     */
    private val _sortType = settingsManager.bookSortType
    val sortType = _sortType.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = BookSortType.ALPHABETICAL
    )

    /**
     * 排序顺序
     */
    private val _sortOrder = settingsManager.bookSortOrder
    val sortOrder = _sortOrder.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = false
    )


    // 是否开启双击返回
    val isDoubleBackToExitEnabled = settingsManager.isDoubleBackToExitEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = false
    )

    // 搜索文本
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    // 过滤状态
    private val _filterStateFlow = combine(
        _sortType,
        _sortOrder,
        _isSearching,
        _searchText
    ) { type, order, searching, text ->
        BookFilterState(type, order, searching, text)
    }

    // 展示给用户的书籍列表
    val categoryWithBooksList = combine(
        _categoryWithBooksList,
        _filterStateFlow,
    ) { categoryWithBookLists, filterState ->
        // 排序
        val sortedCategoryWithBookList = categoryWithBookLists.map { categoryWithBookList ->
            val books =
                sortBooks(
                    categoryWithBookList.bookList,
                    filterState.sortType,
                    filterState.isAscending
                )
            val category = categoryWithBookList.category
            CategoryWithBookList(
                category = category,
                bookList = books
            )
        }.sortedBy { it.category.id }

        // 如果是搜索模式, 则进行搜索过滤
        val filteredCategoryWithBookList = if (filterState.isSearching) {
            sortedCategoryWithBookList.map { categoryWithBookList ->
                val books = filterBooks(categoryWithBookList.bookList, filterState.searchText)
                CategoryWithBookList(
                    category = categoryWithBookList.category,
                    bookList = books
                )
            }
        } else {
            sortedCategoryWithBookList
        }

        return@combine filteredCategoryWithBookList
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = emptyList()
    )

    // 顶部栏状态
    val topBarState = combine(
        _selectedBooks,
        _isSearching,
    ) { selectedBooks, isSearching ->
        // 优先级: 多选模式 > 搜索模式 > 默认模式
        if (selectedBooks.isNotEmpty()) {
            return@combine LibraryTopBarState.SELECT_BOOK
        } else if (isSearching) {
            return@combine LibraryTopBarState.SEARCH_BOOK
        }
        return@combine LibraryTopBarState.DEFAULT
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        LibraryTopBarState.DEFAULT
    )

    private val _effects: MutableSharedFlow<LibraryEffect> = MutableSharedFlow()
    val effects: SharedFlow<LibraryEffect> = _effects.asSharedFlow()

    // --- 设置相关 ---
    /**
     * 书籍显示模式
     */
    val bookDisplayMode = settingsManager.bookDisplayMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = DisplayMode.List
    )

    /**
     * 书籍网格大小
     */
    val bookGridColumnCount = settingsManager.bookGridColumnCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = AppConstants.GRID_SIZE_INT_RANGE.first
    )

    /**
     * 书籍标题位置
     */
    val bookTitlePosition = settingsManager.bookTitlePosition.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = BookTitlePosition.Below
    )

    /**
     * 显示阅读按钮
     */
    val showReadButton = settingsManager.showReadButton.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = true
    )

    /**
     * 显示阅读进度
     */
    val showReadProgress = settingsManager.showReadProgress.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = true
    )

    /**
     * 显示分类标签页
     */
    val showCategoryTab = settingsManager.showCategoryTab.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = true
    )

    /**
     * 总是显示默认分类标签页
     */
    val alwaysShowDefaultCategoryTab = settingsManager.alwaysShowDefaultCategoryTab.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = true
    )

    /**
     * 是否显示书籍数量
     */
    val showBookCount = settingsManager.showBookCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = true
    )

    fun onEvent(event: LibraryEvent) {
        viewModelScope.launch {
            when (event) {
                is LibraryEvent.OnBookClicked -> {
                    if (isSelectionMode.value) {
                        // 选中模式: 切换选中状态
                        toggleBookSelection(bookId = event.bookId)
                    } else {
                        // 非选中模式: 导航到书籍详情
                        _effects.emit(OnNavigateToBookDetail(event.bookId))
                    }
                }

                is LibraryEvent.OnBookLongClicked -> {
                    // 长按: 进入选中模式, 且选中该项
                    toggleBookSelection(bookId = event.bookId)
                }

                is LibraryEvent.OnAboutClicked -> {
                    _effects.emit(OnNavigateToAbout)
                }

                is LibraryEvent.OnHelpClicked -> {
                    _effects.emit(OnNavigateToHelp)
                }

                is LibraryEvent.OnMoreClicked -> {
                    _effects.emit(OpenMoreOptionsBottomSheet)
                }

                is LibraryEvent.OnStartReadingClicked -> {

                }

                is LibraryEvent.OnAddBookClicked -> {
                    _effects.emit(OnNavigateToBrowser)
                }

                LibraryEvent.OnCancelSelectionClicked -> {
                    // 取消选中模式
                    clearBookSelection()
                }

                LibraryEvent.OnDeleteClicked -> {
                    // 打开删除确认面板
                    _effects.emit(ShowDeleteBookConfirmDialog)
                }

                LibraryEvent.OnSelectAllClicked -> {
                    // 选中所有书籍
                    selectAllBooks()
                }

                is LibraryEvent.OnSearchTextChanged -> {
                    // 更新搜索文本
                    _searchText.value = event.text
                }

                LibraryEvent.OnSearchClicked -> {
                    // 进入搜索模式
                    _isSearching.value = true
                }

                LibraryEvent.OnExitSearchClicked -> {
                    // 退出搜索模式
                    _isSearching.value = false
                }

                LibraryEvent.OnFilterClicked -> {
                    // 打开过滤器
                    _effects.emit(OpenFilterBottomSheet)
                }

                LibraryEvent.OnMoveCategoryClicked -> {
                    // 打开移动书籍分类面板
                    _effects.emit(LibraryEffect.OpenMoveBookCategoryDialog)
                }

                LibraryEvent.OnSettingsClicked -> {
                    _effects.emit(OnNavigateToSettings)
                }

                LibraryEvent.OnDeleteDialogConfirmed -> {
                    if (_selectedBooks.value.isEmpty()) {
                        logE {
                            "删除书籍失败：未选择任何书籍"
                        }
                        return@launch
                    }
                    // 删除书籍成功数
                    var successCount = 0

                    // 删除所选书籍
                    _selectedBooks.value.forEach { bookId ->
                        // 删除书籍
                        deleteBook(bookId)
                        successCount++
                    }
                    if (successCount == _selectedBooks.value.size) {
                        _effects.emit(ShowDeleteBookSuccessToast)
                    } else {
                        _effects.emit(ShowDeleteBookCountToast(successCount))
                    }

                    // 清空选中列表
                    _selectedBooks.value = emptySet()
                    _effects.emit(CloseDeleteBookConfirmDialog)
                }

                LibraryEvent.OnDeleteDialogDismissed -> {
                    _effects.emit(CloseDeleteBookConfirmDialog)
                }

                LibraryEvent.OnFilterBottomSheetDismissed -> {
                    _effects.emit(CloseFilterBottomSheet)
                }


                is LibraryEvent.OnSortChanged -> {
                    settingsManager.setBookSortType(event.sortType)
                    settingsManager.setBookSortOrder(event.isAscending)
                }

                is LibraryEvent.OnDisplayModeChanged -> {
                    settingsManager.setBookDisplayMode(event.displayMode)
                }

                is LibraryEvent.OnGridSizeChanged -> {
                    settingsManager.setBookGridColumnCount(event.gridSize)
                }

                is LibraryEvent.OnBookTitlePositionChanged -> {
                    settingsManager.setBookTitlePosition(event.titlePosition)
                }

                is LibraryEvent.OnReadButtonVisibleChanged -> {
                    settingsManager.setShowReadButton(event.visible)
                }

                is LibraryEvent.OnProgressVisibleChanged -> {
                    settingsManager.setShowReadProgress(event.visible)
                }

                is LibraryEvent.OnCategoryTabVisibleChanged -> {
                    settingsManager.setShowCategoryTab(event.visible)
                }

                is LibraryEvent.OnDefaultCategoryTabVisibleChanged -> {
                    settingsManager.setAlwaysShowDefaultCategoryTab(event.visible)
                }

                is LibraryEvent.OnBookCountVisibleChanged -> {
                    settingsManager.setShowBookCount(event.visible)
                }

                is LibraryEvent.OnMoveCategoryDialogConfirmed -> {
                    val selectedIds = event.selectedCategoryIds.toMutableList()
                    if (selectedIds.isEmpty()) {
                        logW {
                            "未选择任何分类，将移至默认分类中"
                        }
                        selectedIds.add(Category.DEFAULT_CATEGORY_ID)
                    }


                    // 将所选书籍移动到指定分类
                    bookRepositoryImpl.moveBooksToCategories(
                        bookIds = selectedBooks.value.toList(),
                        categoryIds = selectedIds
                    )
                    // 关闭移动书籍分类面板，并清空所选书籍
                    clearBookSelection()
                    _effects.emit(LibraryEffect.CloseMoveBookCategoryDialog)
                }

                LibraryEvent.OnMoveCategoryDialogDismissed -> {
                    // 关闭移动书籍分类面板
                    _effects.emit(LibraryEffect.CloseMoveBookCategoryDialog)
                }

                LibraryEvent.OnMoveCategoryDialogEditClicked -> {
                    // 导航至书库设置页
                    _effects.emit(LibraryEffect.OnNavigateToLibrarySettings)
                    _effects.emit(LibraryEffect.CloseMoveBookCategoryDialog)
                }

                is LibraryEvent.OnReadButtonClicked -> {
                    _effects.emit(LibraryEffect.OnNavigateToReaderScreen(event.bookId))
                }
            }
        }

    }


    /**
     * 修改书籍的选中状态
     */
    fun toggleBookSelection(bookId: Long) {
        val newSelection = _selectedBooks.value.toMutableSet()
        if (bookId in newSelection) {
            newSelection.remove(bookId)
        } else {
            newSelection.add(bookId)
        }
        _selectedBooks.value = newSelection
    }

    /**
     * 取消所有书籍的选中状态
     */
    fun clearBookSelection() {
        _selectedBooks.value = emptySet()
    }

    /**
     * 选中所有的书籍
     */
    fun selectAllBooks() {
        val allIds = rawBooks.value.map { it.id }.toSet()
        _selectedBooks.value = allIds
    }

    /**
     * 对书籍进行排序
     */
    private fun sortBooks(
        book: List<Book>,
        sortType: BookSortType,
        isAscending: Boolean
    ): List<Book> {
        return if (isAscending) {
            when (sortType) {
                BookSortType.ALPHABETICAL -> book.sortedBy { it.title.lowercase() }
                BookSortType.LAST_READ_TIME -> book.sortedBy { it.lastReadTime }
                BookSortType.PROGRESS -> book.sortedBy { it.progress }
                BookSortType.AUTHOR -> book.sortedBy { it.author.first().lowercase() }
            }
        } else {
            when (sortType) {
                BookSortType.ALPHABETICAL -> book.sortedByDescending { it.title.lowercase() }
                BookSortType.LAST_READ_TIME -> book.sortedByDescending { it.lastReadTime }
                BookSortType.PROGRESS -> book.sortedByDescending { it.progress }
                BookSortType.AUTHOR -> book.sortedByDescending { it.author.first().lowercase() }
            }
        }
    }

    /**
     * 对书籍进行文本过滤
     */
    private fun filterBooks(bookEntities: List<Book>, searchText: String): List<Book> {
        return bookEntities.filter { book ->
            book.title.contains(searchText, ignoreCase = true)
        }
    }
}