package io.github.lycosmic.lithe.presentation.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.lycosmic.domain.model.Book
import io.github.lycosmic.domain.model.BookChapter
import io.github.lycosmic.domain.model.ReadingProgress
import io.github.lycosmic.domain.use_case.browse.GetBookUseCase
import io.github.lycosmic.domain.use_case.reader.GetChapterContentUseCase
import io.github.lycosmic.domain.use_case.reader.GetChapterListUseCase
import io.github.lycosmic.domain.use_case.reader.GetReadingProgressUseCase
import io.github.lycosmic.domain.use_case.reader.SaveReadingProgressUseCase
import io.github.lycosmic.lithe.log.logD
import io.github.lycosmic.lithe.log.logE
import io.github.lycosmic.lithe.log.logI
import io.github.lycosmic.lithe.log.logW
import io.github.lycosmic.lithe.presentation.reader.mapper.ContentMapper
import io.github.lycosmic.lithe.presentation.reader.model.ReaderContent
import io.github.lycosmic.lithe.presentation.reader.model.ReaderEffect
import io.github.lycosmic.lithe.presentation.reader.model.ReaderEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val getChapterListUseCase: GetChapterListUseCase,
    private val getChapterContentUseCase: GetChapterContentUseCase,
    private val getReadingProgressUseCase: GetReadingProgressUseCase,
    private val saveReadingProgressUseCase: SaveReadingProgressUseCase,
    private val getBookUseCase: GetBookUseCase,
) : ViewModel() {

    // 书籍 ID
    private val _bookIdFlow = MutableStateFlow<Long?>(null)

    // 滚动事件
    private val scrollEventFlow = MutableSharedFlow<ReaderContent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    // 手动状态源
    private val _transientState = MutableStateFlow(ReaderTransientState())

    // 自动数据流
    @OptIn(ExperimentalCoroutinesApi::class)
    private val _persistentDataFlow = _bookIdFlow.filterNotNull().flatMapLatest { bookId ->
        combine(
            getBookUseCase(bookId),
            getReadingProgressUseCase(bookId)
        ) { bookResult, progressResult ->
            val book = bookResult.getOrNull()
            val progress = progressResult.getOrNull() ?: ReadingProgress.default(bookId)

            if (book == null) {
                return@combine ReaderPersistentData(error = "书籍不存在")
            }

            // 加载章节列表
            val chapters = getChapterListUseCase(book).getOrDefault(emptyList())
            if (chapters.isEmpty()) {
                return@combine ReaderPersistentData(error = "书籍没有章节")
            }

            // 加载当前章节内容
            val currentContent = run {
                val chapter = chapters.getOrNull(progress.chapterIndex)
                if (chapter != null) {
                    getChapterContentUseCase(book.fileUri, book.format, chapter)
                        .getOrNull()?.map { ContentMapper.mapToUi(progress.chapterIndex, it) }
                        ?: return@combine ReaderPersistentData(error = "章节内容不存在")
                } else {
                    return@combine ReaderPersistentData(error = "章节不存在")
                }
            }

            ReaderPersistentData(
                book = book,
                chapters = chapters,
                currentContent = currentContent,
                progress = progress
            )
        }
    }
    val uiState: StateFlow<ReaderUiState> = combine(
        _persistentDataFlow,
        _transientState
    ) { data, transient ->
        ReaderUiState(
            // 来自数据库的数据
            bookId = data.book?.id ?: -1L,
            bookName = data.book?.title ?: "",
            chapters = data.chapters,
            readerItems = data.currentContent,
            isInitialLoading = false,
            error = data.error ?: transient.transientError,
            progress = data.progress ?: ReadingProgress.default(-1L),
            currentContent = transient.visibleContent
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = ReaderUiState(isInitialLoading = true)
    )


    private data class ReaderPersistentData(
        val book: Book? = null,
        val chapters: List<BookChapter> = emptyList(),
        val currentContent: List<ReaderContent> = emptyList(),
        val progress: ReadingProgress? = null,
        val error: String? = null
    )

    private data class ReaderTransientState(
        val chapterProgressText: String? = null,
        val bookProgressText: String? = null,
        val transientError: String? = null,
        val visibleContent: ReaderContent? = null,
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

    /**
     * 找到之前的滚动位置
     */
    fun findScrollPosition(contents: List<ReaderContent>, targetCharIndex: Int): Int {
        var index = contents.indexOfLast { it.startIndex <= targetCharIndex }

        if (index == -1) {
            logW {
                "找不到滚动位置，将滚动到开头"
            }
            index = 0
        }
        return index
    }

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

                is ReaderEvent.OnChapterItemClick -> {
                    switchToChapter(bookId = event.bookId, index = event.index)
                }

                is ReaderEvent.OnChapterChanged -> TODO()
                is ReaderEvent.OnScrollPositionChanged -> {
                    updateMemoryProgress(event.visibleContent)
                }

                is ReaderEvent.OnStopOrDispose -> {
                    saveToDbImmediate(bookId = event.bookId)
                }
            }
        }

    }

    /**
     * 更新内存中的进度文本
     */
    private fun updateMemoryProgress(content: ReaderContent) {
        viewModelScope.launch {
            _transientState.update {
                // TODO：目前简单实现
                it.copy(
                    chapterProgressText = "${content.chapterIndex + 1}/${uiState.value.chapters.size}",
                    bookProgressText = "${content.startIndex + 1}/${uiState.value.readerItems.size}",
                    visibleContent = content
                )
            }
        }
    }


    /**
     * 切换章节
     */
    fun switchToChapter(bookId: Long, index: Int) {
        if (bookId == -1L) {
            logE {
                "切换章节失败，书籍ID为-1"
            }
            return
        }
    }

    /**
     * 用于退出时保存进度
     */
    private fun saveToDbImmediate(bookId: Long) {
        logI {
            "退出时保存进度"
        }

        val currentContent = uiState.value.currentContent
        if (currentContent != null) {
            saveToDb(bookId, content = currentContent)
        } else {
            logE {
                "退出时保存进度失败，当前章节内容为空"
            }
        }
    }


    /**
     * 数据库保存进度
     */
    private fun saveToDb(
        bookId: Long,
        content: ReaderContent,
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val progress = ReadingProgress(
                bookId = bookId,
                chapterIndex = content.chapterIndex,
                chapterOffsetCharIndex = content.startIndex
            )
            saveReadingProgressUseCase(
                bookId = bookId,
                progress = progress
            ).onFailure {
                // 保存失败
                logE {
                    "保存进度失败"
                }
                return@launch
            }

            logD {
                "保存进度成功，进度：${progress}"
            }
        }
    }
}