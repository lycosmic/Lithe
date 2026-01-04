package io.github.lycosmic.lithe.presentation.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.lycosmic.domain.use_case.browse.GetBookUseCase
import io.github.lycosmic.domain.use_case.reader.GetChapterContentUseCase
import io.github.lycosmic.domain.use_case.reader.GetChapterListUseCase
import io.github.lycosmic.domain.use_case.reader.GetReadingProgressUseCase
import io.github.lycosmic.lithe.presentation.reader.mapper.ContentMapper
import io.github.lycosmic.lithe.presentation.reader.model.ReaderEffect
import io.github.lycosmic.lithe.presentation.reader.model.ReaderEvent
import io.github.lycosmic.lithe.util.AppConstants
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val getChapterListUseCase: GetChapterListUseCase,
    private val getChapterContentUseCase: GetChapterContentUseCase,
    private val getReadingProgressUseCase: GetReadingProgressUseCase,
    private val getBookUseCase: GetBookUseCase,
) : ViewModel() {

    // 书籍 ID
    private val _bookIdFlow = MutableStateFlow<Long?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<ReaderUiState> = _bookIdFlow
        .filterNotNull()
        .flatMapLatest { id ->
            // 书籍信息，ID 变化时会自动重新获取
            getBookUseCase(id).flatMapLatest { bookResult ->
                val book = bookResult.getOrNull()
                    ?: return@flatMapLatest flowOf(ReaderUiState(error = "书籍不存在，ID: $id"))

                // 章节列表
                val chapters = getChapterListUseCase(book).getOrDefault(emptyList())

                // 监听阅读进度
                getReadingProgressUseCase(book.id)
                    .distinctUntilChanged { old, new ->
                        old.getOrNull()?.chapterIndex == new.getOrNull()?.chapterIndex
                    }
                    .transform { progressResult ->
                        val progress = progressResult.getOrNull()
                        val chapterIndex = progress?.chapterIndex ?: 0

                        // 防止进度越界
                        val currentChapter = chapters.getOrNull(chapterIndex)
                        if (currentChapter == null) {
                            emit(
                                ReaderUiState(
                                    book = book,
                                    chapters = chapters,
                                    error = "找不到对应章节"
                                )
                            )
                            return@transform
                        }

                        // 获取章节内容
                        val contentResult = getChapterContentUseCase(
                            book.fileUri,
                            book.format,
                            currentChapter
                        )

                        val rawContents = contentResult.getOrNull() ?: emptyList()

                        emit(
                            ReaderUiState(
                                book = book,
                                chapters = chapters,
                                currentProgress = progress,
                                currentContent = rawContents.map { ContentMapper.mapToUi(it) },
                                isLoading = false,
                                error = contentResult.exceptionOrNull()?.message
                            )
                        )
                    }
            }
        }.stateIn(
            scope = viewModelScope,
            initialValue = ReaderUiState(isLoading = true),
            started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT)
        )


    private val _effects = MutableSharedFlow<ReaderEffect>()
    val effects = _effects.asSharedFlow()

    /**
     * 设置书籍 ID
     */
    fun setBookId(id: Long) {
        viewModelScope.launch {
            _bookIdFlow.value = id
        }
    }

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
        viewModelScope.launch {
            when (event) {
                ReaderEvent.OnReadContentClick -> {
                    _effects.emit(ReaderEffect.ShowOrHideTopBarAndBottomControl)
                }

                ReaderEvent.OnBackClick -> {
                    _effects.emit(ReaderEffect.NavigateBack)
                }

                ReaderEvent.OnChapterMenuClick -> {
                    _effects.emit(ReaderEffect.ShowChapterListDrawer)
                }

                ReaderEvent.OnDrawerBackClick -> {
                    _effects.emit(ReaderEffect.HideChapterListDrawer)
                }
            }
        }

    }
}