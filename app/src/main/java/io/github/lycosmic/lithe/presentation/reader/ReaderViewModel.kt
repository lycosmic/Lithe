package io.github.lycosmic.lithe.presentation.reader

import android.app.Application
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.lycosmic.lithe.data.model.content.BookSpineItem
import io.github.lycosmic.lithe.data.parser.content.BookContentParser
import io.github.lycosmic.lithe.data.parser.content.BookContentParserFactory
import io.github.lycosmic.lithe.data.repository.BookRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val application: Application,
    private val bookRepository: BookRepository,
    private val bookContentParserFactory: BookContentParserFactory
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState = _uiState.asStateFlow()

    private var bookContentParser: BookContentParser? = null

    /**
     * 加载书籍
     */
    fun loadBook(bookId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.i("Start loading books, book ID: %s", bookId.toString())
            try {
                val book = bookRepository.getBookById(bookId)
                if (book == null) {
                    _uiState.update {
                        it.copy(isLoading = false, error = "书籍不存在")
                    }
                    return@launch
                }

                // 获取目录结构
                val spine = book.bookmarks ?: emptyList()

                _uiState.update {
                    it.copy(
                        book = book,
                        isLoading = true,
                        spine = spine,
                    )
                }


                // 获取解析器
                bookContentParser = bookContentParserFactory.getParser(book.format)
                val bookUri = book.fileUri.toUri()


                // 默认加载第一章
                val initialIndex = 0

                Timber.i(
                    "Starts loading the contents of the initial chapter with the chapter number %d",
                    initialIndex
                )
                // 加载章节内容
                bookContentParser?.let {
                    loadChapterContent(it, bookUri, spine, initialIndex)
                }
            } catch (e: Exception) {
                Timber.e(e, "Books fail to load")
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private suspend fun loadChapterContent(
        parser: BookContentParser,
        bookUri: Uri,
        spine: List<BookSpineItem>,
        index: Int
    ) {
        _uiState.update { it.copy(isLoading = true) }

        val chapterItem = spine[index]
        // 调用解析器解析具体内容
        val content = parser.parseChapterContent(application, bookUri, chapterItem.contentHref)

        _uiState.update {
            it.copy(
                isLoading = false,
                currentChapterIndex = index,
                currentContent = content
            )
        }
    }

    // --- 翻页逻辑 ---
    fun loadNextChapter() {
        val currentState = _uiState.value
        val book = currentState.book ?: return
        val nextIndex = currentState.currentChapterIndex + 1

        if (nextIndex < currentState.spine.size) {
            viewModelScope.launch(Dispatchers.IO) {
                bookContentParser?.let { parser ->
                    loadChapterContent(parser, book.fileUri.toUri(), currentState.spine, nextIndex)
                }

            }
        }
    }

    fun loadPrevChapter() {
        val currentState = _uiState.value
        val book = currentState.book ?: return
        val prevIndex = currentState.currentChapterIndex - 1

        if (prevIndex >= 0) {
            viewModelScope.launch(Dispatchers.IO) {
                bookContentParser?.let { parser ->
                    loadChapterContent(parser, book.fileUri.toUri(), currentState.spine, prevIndex)
                }
            }
        }
    }
}