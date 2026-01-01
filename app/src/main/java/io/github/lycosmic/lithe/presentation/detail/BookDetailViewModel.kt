package io.github.lycosmic.lithe.presentation.detail

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.lycosmic.lithe.R
import io.github.lycosmic.lithe.data.local.entity.Book
import io.github.lycosmic.lithe.data.model.Constants
import io.github.lycosmic.lithe.data.repository.BookRepositoryImpl
import io.github.lycosmic.lithe.utils.UriUtils
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val bookRepositoryImpl: BookRepositoryImpl,
) : ViewModel() {

    private val _book = MutableStateFlow(Book.defaultBook)
    val book = _book.asStateFlow()

    // 自己维护的文件路径, 用来判断文件是否为空, 或有损坏
    private val _filePath = MutableStateFlow<String?>(null)
    val filePath = _filePath.asStateFlow()

    private val _effects = MutableSharedFlow<BookDetailEffect>()
    val effects = _effects.asSharedFlow()


    fun loadBook(bookId: Long) {
        viewModelScope.launch {
            bookRepositoryImpl.getBookFlowById(bookId).stateIn(
                scope = viewModelScope,
                started = WhileSubscribed(Constants.STATE_FLOW_STOP_TIMEOUT_MILLIS),
                initialValue = Book.defaultBook
            ).filterNotNull().collect { book ->
                _book.value = book

                book.let {
                    // 初始的文件路径
                    val path = UriUtils.parseUriToPath(book.fileUri.toUri())
                    _filePath.value = "${path.first}/${path.second}"
                }

            }
        }
    }

    fun onEvent(event: BookDetailEvent) {
        viewModelScope.launch {
            when (event) {
                // 返回书库页面
                BookDetailEvent.OnBackClicked -> {
                    _effects.emit(BookDetailEffect.NavigateBack)
                }

                // 点击删除书籍按钮
                is BookDetailEvent.OnDeleteClicked -> {
                    _effects.emit(BookDetailEffect.ShowDeleteBookDialog)
                }

                // 点击移动书籍按钮
                BookDetailEvent.OnMoveClicked -> {
                    _effects.emit(BookDetailEffect.ShowMoveBookDialog)
                }

                // 点击取消删除书籍按钮
                BookDetailEvent.OnCancelDeleteClicked -> {
                    _effects.emit(BookDetailEffect.DismissDeleteBookDialog)
                }

                // 点击确认删除书籍按钮
                BookDetailEvent.OnConfirmDeleteClicked -> {
                    deleteBook()
                }

                // 点击删除书籍对话框外部
                is BookDetailEvent.OnDeleteDialogDismissed -> {
                    _effects.emit(BookDetailEffect.DismissDeleteBookDialog)
                }
            }
        }
    }

    /**
     * 删除书籍
     */
    fun deleteBook() {
        viewModelScope.launch {
            _effects.emit(BookDetailEffect.DismissDeleteBookDialog)
            bookRepositoryImpl.deleteBook(book.value.id)
            _effects.emit(BookDetailEffect.ShowBookDeletedToast(R.string.delete_book_success))
            _effects.emit(BookDetailEffect.NavigateBack)
        }
    }
}