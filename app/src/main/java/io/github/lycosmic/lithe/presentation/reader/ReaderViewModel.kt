package io.github.lycosmic.lithe.presentation.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.lycosmic.domain.use_case.browse.GetBookUseCase
import io.github.lycosmic.domain.use_case.reader.GetChapterContentUseCase
import io.github.lycosmic.domain.use_case.reader.GetChapterListUseCase
import io.github.lycosmic.lithe.log.logD
import io.github.lycosmic.lithe.log.logE
import io.github.lycosmic.lithe.log.logI
import io.github.lycosmic.lithe.presentation.reader.components.ReaderEffect
import io.github.lycosmic.lithe.presentation.reader.components.ReaderEvent
import io.github.lycosmic.lithe.presentation.reader.mapper.ContentMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val getChapterListUseCase: GetChapterListUseCase,
    private val getChapterContentUseCase: GetChapterContentUseCase,
    private val getBookUseCase: GetBookUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<ReaderEffect>()
    val effects = _effects.asSharedFlow()

    /**
     * 加载书籍
     */
    fun loadBook(bookId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                it.copy(
                    isLoading = true,
                )
            }

            logI {
                "开始加载书籍，书籍ID: $bookId"
            }
            try {
                val book = getBookUseCase(bookId).getOrElse {
                    logE(e = it) {
                        "加载书籍失败，ID: $bookId"
                    }
                    return@launch
                }
                _uiState.update {
                    it.copy(
                        book = book,
                    )
                }

                // 获取目录结构
                val chapters = getChapterListUseCase(book).getOrElse { throwable ->
                    logE(e = throwable) {
                        "获取目录结构失败"
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                        )
                    }
                    return@launch
                }

                if (chapters.isEmpty()) {
                    logE {
                        "目录结构为空"
                    }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                        )
                    }
                    return@launch
                }

                logD {
                    "获取目录结构成功，目录结构: $chapters"
                }
                _uiState.update {
                    it.copy(
                        chapters = chapters,
                    )
                }


                val contentBlockList = getChapterContentUseCase(
                    book.fileUri,
                    book.format,
                    chapters[0] // TODO: 暂时默认加载第一章
                ).getOrElse { throwable ->
                    logE(e = throwable) {
                        "获取章节内容失败"
                    }
                    return@launch
                }

                val readerContents = contentBlockList.map {
                    ContentMapper.mapToUi(
                        it
                    )
                }

                _uiState.update {
                    it.copy(
                        currentContent = readerContents,
                        isLoading = false,
                    )
                }
            } catch (e: Exception) {
                logE(e = e) {
                    "书籍加载失败，ID: $bookId"
                }
            }
        }
    }

//    private suspend fun loadChapterContent(
//        parser: BookContentParser,
//        bookUri: Uri,
//        spine: List<BookSpineItem>,
//        index: Int
//    ) {
//        _uiState.update { it.copy(isLoading = true) }
//
//        if (spine.isEmpty()) {
//            logE {
//                "Spine is empty, book ID: ${_uiState.value.book?.id}, chapter index: $index"
//            }
//            return
//        }
//        val chapterItem = spine[index]
//        // 调用解析器解析具体内容
//        val content = when (chapterItem) {
//            is EpubSpineItem -> parser.loadChapterContent(bookUri, chapterItem.contentHref)
//            is TxtSpineItem -> parser.loadChapterContent(bookUri, chapterItem.href)
//        }
//        logD {
//            "Chapter content is $content"
//        }
//
//        logI {
//            "Chapter content loaded successfully, book ID: ${_uiState.value.bookEntity?.id}, chapter index: $index"
//        }
//        _uiState.update {
//            it.copy(
//                isLoading = false,
//                currentChapterIndex = index,
//                currentContent = content
//            )
//        }
//    }

//    // --- 翻页逻辑 ---
//    fun loadNextChapter() {
//        val currentState = _uiState.value
//        val book = currentState.book ?: return
//        val nextIndex = currentState.currentChapterIndex + 1
//
//        if (nextIndex < currentState.chapters.size) {
//            viewModelScope.launch(Dispatchers.IO) {
//                loadChapterContent(bookContentParser, book.fileUri.toUri(), currentState.spine, nextIndex)
//            }
//        }
//    }
//
//    fun loadPrevChapter() {
//        val currentState = _uiState.value
//        val book = currentState.bookEntity ?: return
//        val prevIndex = currentState.currentChapterIndex - 1
//
//        if (prevIndex >= 0) {
//            viewModelScope.launch(Dispatchers.IO) {
//                bookContentParser?.let { parser ->
//                    loadChapterContent(parser, book.fileUri.toUri(), currentState.spine, prevIndex)
//                }
//            }
//        }
//    }

    fun onEvent(event: ReaderEvent) {
        when (event) {
            ReaderEvent.OnContentClick -> {
                viewModelScope.launch {
                    _effects.emit(ReaderEffect.ShowOrHideTopBarAndBottomControl)
                }
            }
        }
    }
}