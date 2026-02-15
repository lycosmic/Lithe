package io.github.lycosmic.lithe.presentation.detail

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.lycosmic.data.util.parsePathInfo
import io.github.lycosmic.domain.model.Book
import io.github.lycosmic.domain.model.ReadingProgress
import io.github.lycosmic.domain.repository.BookRepository
import io.github.lycosmic.domain.repository.ProgressRepository
import io.github.lycosmic.lithe.R
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val progressRepository: ProgressRepository,
) : ViewModel() {

    private val _book = MutableStateFlow(Book.default)
    val book = _book.asStateFlow()

    // 自己维护的文件路径, 用来判断文件是否为空, 或有损坏
    private val _filePath = MutableStateFlow<String?>(null)
    val filePath = _filePath.asStateFlow()

    private val _effects = MutableSharedFlow<BookDetailEffect>()
    val effects = _effects.asSharedFlow()

    /**
     * 初始加载书籍
     */
    fun loadBook(bookId: Long) {
        viewModelScope.launch {
            combine(
                bookRepository.getBookFlowById(bookId).mapNotNull {
                    it.getOrNull() ?: Book.default
                },
                progressRepository.getBookProgressFlow(bookId).mapNotNull {
                    it.getOrNull() ?: ReadingProgress.default(bookId)
                }
            ) { book, progress ->
                book.copy(
                    progress = progress.progressPercent,
                    lastReadTime = progress.lastReadTime
                )
            }.collect { book ->
                _book.update {
                    book
                }

                // 更新文件路径
                book.let {
                    // 初始的文件路径
                    val path = book.fileUri.toUri().parsePathInfo().getOrElse {
                        // TODO: 文件损坏
                        return@collect
                    }
                    _filePath.value = "${path.first}/${path.second}"
                }
            }
        }
    }

    /**
     *
     */

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
            bookRepository.deleteBook(book.value.id)
            _effects.emit(BookDetailEffect.ShowBookDeletedToast(R.string.delete_book_success))
            _effects.emit(BookDetailEffect.NavigateBack)
        }
    }
}