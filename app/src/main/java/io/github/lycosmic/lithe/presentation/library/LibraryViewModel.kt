package io.github.lycosmic.lithe.presentation.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.lycosmic.lithe.data.model.Constants
import io.github.lycosmic.lithe.data.repository.BookRepository
import io.github.lycosmic.lithe.presentation.library.LibraryEffect.OnNavigateToAbout
import io.github.lycosmic.lithe.presentation.library.LibraryEffect.OnNavigateToBookDetail
import io.github.lycosmic.lithe.presentation.library.LibraryEffect.OnNavigateToBrowser
import io.github.lycosmic.lithe.presentation.library.LibraryEffect.OnNavigateToHelp
import io.github.lycosmic.lithe.presentation.library.LibraryEffect.OnNavigateToSettings
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    // 书籍列表
    val books = bookRepository.getAllBooks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(Constants.STATE_FLOW_STOP_TIMEOUT_MILLIS),
        initialValue = emptyList()
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

                is LibraryEvent.OnSettingsClicked -> {
                    _effects.emit(OnNavigateToSettings)
                }

                is LibraryEvent.OnStartReadingClicked -> TODO()
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

                LibraryEvent.OnMoveClicked -> {
                    TODO()
                }

                LibraryEvent.OnSelectAllClicked -> {
                    // 选中所有书籍
                    selectAllBooks()
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