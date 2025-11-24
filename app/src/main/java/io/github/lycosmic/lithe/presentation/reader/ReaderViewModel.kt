package io.github.lycosmic.lithe.presentation.reader

import android.app.Application
import android.net.Uri
import android.util.Log
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
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val application: Application,
    private val bookRepository: BookRepository,
    private val bookContentParserFactory: BookContentParserFactory
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState = _uiState.asStateFlow()

    /**
     * 加载书籍
     */
    fun loadBook(bookId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val book = bookRepository.getBookById(bookId)
                if (book == null) {
                    _uiState.update {
                        it.copy(isLoading = false, error = "书籍不存在")
                    }
                    return@launch
                }

                _uiState.update { it.copy(book = book, isLoading = true) }

                // 获取解析器
                val parser = bookContentParserFactory.getParser(book.format)
                val bookUri = book.fileUri.toUri()

                // 解析目录结构
                val spine = parser.parseSpine(application, bookUri)
                if (spine.isEmpty()) {
                    _uiState.update {
                        it.copy(isLoading = false, error = "解析目录失败")
                    }
                    return@launch
                }

                // 默认加载第一章
                // (这里简化处理：默认第0章。实际应该从 book.lastReadPosition 恢复)
                val initialIndex = 0

                // 5. 加载章节内容
                loadChapterContent(parser, bookUri, spine, initialIndex)

            } catch (e: Exception) {
                e.printStackTrace()
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
        val content = parser.parseContent(application, bookUri, chapterItem.href)

        _uiState.update {
            it.copy(
                isLoading = false,
                spine = spine,
                currentChapterIndex = index,
                currentContent = content
            )
        }
        Log.d("ReaderViewModel", "loadChapterContent: chapterItem:$chapterItem, content:$content")
    }

    // --- 翻页逻辑 ---
    fun loadNextChapter() {
        val currentState = _uiState.value
        val book = currentState.book ?: return
        val nextIndex = currentState.currentChapterIndex + 1

        if (nextIndex < currentState.spine.size) {
            viewModelScope.launch(Dispatchers.IO) {
                val parser = bookContentParserFactory.getParser(book.format)
                loadChapterContent(parser, book.fileUri.toUri(), currentState.spine, nextIndex)
            }
        }
    }

    fun loadPrevChapter() {
        val currentState = _uiState.value
        val book = currentState.book ?: return
        val prevIndex = currentState.currentChapterIndex - 1

        if (prevIndex >= 0) {
            viewModelScope.launch(Dispatchers.IO) {
                val parser = bookContentParserFactory.getParser(book.format)
                loadChapterContent(parser, book.fileUri.toUri(), currentState.spine, prevIndex)
            }
        }
    }
}