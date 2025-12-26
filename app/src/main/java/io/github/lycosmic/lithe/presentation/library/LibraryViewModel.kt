package io.github.lycosmic.lithe.presentation.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.lycosmic.lithe.data.model.Constants
import io.github.lycosmic.lithe.data.repository.BookRepository
import io.github.lycosmic.lithe.data.settings.SettingsManager
import io.github.lycosmic.lithe.presentation.library.LibraryEffect.OnNavigateToAbout
import io.github.lycosmic.lithe.presentation.library.LibraryEffect.OnNavigateToBookDetail
import io.github.lycosmic.lithe.presentation.library.LibraryEffect.OnNavigateToBrowser
import io.github.lycosmic.lithe.presentation.library.LibraryEffect.OnNavigateToHelp
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val bookRepository: BookRepository,
    settingsManager: SettingsManager
) : ViewModel() {

    // 原始的书籍列表
    private val _rawBooks = bookRepository.getAllBooks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(Constants.STATE_FLOW_STOP_TIMEOUT_MILLIS),
        initialValue = emptyList()
    )

    // 总书籍数
    val totalBooksCount = _rawBooks.map { it.size }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(Constants.STATE_FLOW_STOP_TIMEOUT_MILLIS),
        initialValue = 0
    )

    // 是否显示书籍数
    val showBookCount = settingsManager.showBookCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(Constants.STATE_FLOW_STOP_TIMEOUT_MILLIS),
        initialValue = false
    )

    // 当前选中的书籍, 存储书籍ID
    private val _selectedBooks = MutableStateFlow<Set<Long>>(emptySet())
    val selectedBooks = _selectedBooks.asStateFlow()

    // 当前是否为选中模式
    val isSelectionMode = _selectedBooks.map { it.isNotEmpty() }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(Constants.STATE_FLOW_STOP_TIMEOUT_MILLIS),
        initialValue = false
    )

    // 当前是否处于搜索模式
    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    // 是否开启双击返回
    val isDoubleBackToExitEnabled = settingsManager.isDoubleBackToExitEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(Constants.STATE_FLOW_STOP_TIMEOUT_MILLIS),
        initialValue = false
    )

    // 搜索文本
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    // 展示给用户的书籍列表
    val books = combine(
        _rawBooks,
        _selectedBooks,
        _isSearching,
        _searchText
    ) { rawBooks, selectedBooks, isSearching, searchText ->
        // 如果是搜索模式, 则进行搜索过滤
        if (isSearching) {
            return@combine rawBooks.filter { book ->
                book.title.contains(searchText, ignoreCase = true)
            }
        }

        return@combine rawBooks
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(Constants.STATE_FLOW_STOP_TIMEOUT_MILLIS),
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
        SharingStarted.WhileSubscribed(Constants.STATE_FLOW_STOP_TIMEOUT_MILLIS),
        LibraryTopBarState.DEFAULT
    )

    private val _effects = MutableSharedFlow<LibraryEffect>()
    val effects = _effects.asSharedFlow()

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
                    _effects.emit(LibraryEffect.OpenMoreOptionsBottomSheet)
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
                    _selectedBooks.value.forEach { bookId ->
                        // 删除书籍
                        bookRepository.deleteBook(bookId)
                    }
                    // 清空选中列表
                    _selectedBooks.value = emptySet()
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
                    _effects.emit(LibraryEffect.OpenFilterBottomSheet)
                }

                LibraryEvent.OnMoveCategoryClicked -> {

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
        val allIds = books.value.map { it.id }.toSet()
        _selectedBooks.value = allIds
    }

}