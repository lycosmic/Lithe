package io.github.lycosmic.lithe.presentation.detail

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.lycosmic.data.util.parsePathInfo
import io.github.lycosmic.domain.model.Book
import io.github.lycosmic.domain.model.Category
import io.github.lycosmic.domain.model.ReadingProgress
import io.github.lycosmic.domain.repository.BookRepository
import io.github.lycosmic.domain.repository.CategoryRepository
import io.github.lycosmic.domain.repository.ProgressRepository
import io.github.lycosmic.lithe.R
import io.github.lycosmic.lithe.log.logW
import io.github.lycosmic.lithe.util.AppConstants
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val progressRepository: ProgressRepository,
    categoryRepository: CategoryRepository
) : ViewModel() {

    private val _book = MutableStateFlow(Book.default)
    val book = _book.asStateFlow()

    // 所有的分类
    private val _categories = categoryRepository.getCategoriesFlow().map {
        it.getOrNull() ?: emptyList()
    }.map {
        it.filter { category ->
            category.id != Category.DEFAULT_CATEGORY_ID
        }
    }
    val categories = _categories.stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = emptyList()
    )


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
                },
                bookRepository.getBookCategoriesFlow(bookId)
            ) { book, progress, categories ->
                book.copy(
                    progress = progress.progressPercent,
                    categories = categories,
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

                is BookDetailEvent.OnConfirmMoveClicked -> {
                    val selectedCategoryIds = event.categoryIds.toMutableList()

                    if (selectedCategoryIds.isEmpty()) {
                        logW {
                            "未选择任何分类，将移动到默认分类中。"
                        }
                        selectedCategoryIds.add(Category.DEFAULT_CATEGORY_ID)
                    }

                    // 移动书籍
                    bookRepository.moveBooksToCategories(
                        listOf(book.value.id),
                        selectedCategoryIds
                    )

                    // 关闭移动书籍对话框
                    _effects.emit(BookDetailEffect.DismissMoveBookDialog)
                    // 显示成功提示
                    _effects.emit(BookDetailEffect.ShowMoveBookSuccessToast)
                }

                BookDetailEvent.OnEditCategoryClicked -> {
                    _effects.emit(BookDetailEffect.NavigateToEditCategory)
                }

                BookDetailEvent.OnMoveDialogDismissed -> {
                    _effects.emit(BookDetailEffect.DismissMoveBookDialog)
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