package io.github.lycosmic.lithe.presentation.reader

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.lycosmic.domain.model.Book
import io.github.lycosmic.domain.model.FileFormat
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val getChapterListUseCase: GetChapterListUseCase,
    private val getChapterContentUseCase: GetChapterContentUseCase,
    private val getReadingProgressUseCase: GetReadingProgressUseCase,
    private val saveReadingProgressUseCase: SaveReadingProgressUseCase,
    private val getBookUseCase: GetBookUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<ReaderState> = MutableStateFlow(ReaderState())
    val uiState = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<ReaderEffect>()
    val effects = _effects.asSharedFlow()

    // 数据库保存的流
    private val _saveProgressFlow = MutableSharedFlow<ReaderContent>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    /**
     * 初始化
     */
    fun init(bookId: Long) {
        viewModelScope.launch(Dispatchers.Default) {
            // 获取书籍
            val book = getBookUseCase(bookId).getOrElse { e ->
                logE(e = e) {
                    "初始化失败，ID为 $bookId 的书籍不存在"
                }
                _uiState.update { state ->
                    state.copy(
                        isInitialLoading = false
                    )
                }
                _effects.emit(ReaderEffect.NavigateBack)
                return@launch
            }

            _uiState.update {
                it.copy(
                    book = book,
                )
            }


            // 加载章节列表
            val chapters = getChapterListUseCase(book).getOrElse {
                logE {
                    "获取章节列表失败，ID为 $bookId 的书籍不存在章节列表"
                }
                return@launch
            }

            _uiState.update {
                it.copy(
                    chapters = chapters,
                )
            }

            // 加载进度
            val progress = getReadingProgressUseCase(bookId).getOrElse {
                logW {
                    "获取进度失败，ID为 $bookId 的书籍不存在进度，将使用默认进度"
                }
                ReadingProgress.default(bookId)
            }

            _uiState.update {
                it.copy(
                    currentChapterIndex = progress.chapterIndex,
                    progress = progress,
                )
            }

            // 加载当前章节内容
            val chapterContents = loadChapterContent(
                chapterIndex = progress.chapterIndex,
                bookFormat = book.format,
                bookFileUri = book.fileUri
            )

            if (chapterContents != null) {
                // 计算总长度
                val totalLength = calculateChapterLength(chapterContents)

                _uiState.update {
                    it.copy(
                        readerItems = chapterContents,
                        isInitialLoading = false,
                        currentChapterLength = totalLength
                    )
                }

                // 滚动到上次阅读位置，恢复阅读进度
                val targetCharIndex = progress.chapterOffsetCharIndex
                val itemIndex = findScrollPosition(chapterContents, targetCharIndex)
                _effects.emit(ReaderEffect.ScrollToItem(itemIndex))
                _effects.emit(ReaderEffect.ScrollToChapter(progress.chapterIndex))
            } else {
                logE {
                    "获取章节内容失败，ID为 $bookId 的书籍不存在章节内容，章节索引为 ${progress.chapterIndex}"
                }
                _uiState.update {
                    it.copy(
                        isInitialLoading = false,
                        error = "章节不存在"
                    )
                }
                return@launch
            }

        }

        viewModelScope.launch(Dispatchers.IO) {
            _saveProgressFlow
                .debounce(3000)
                .collect { content ->
                    saveToDb(bookId, content)
                }
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
                    switchToChapter(index = event.index)
                }

                is ReaderEvent.OnScrollPositionChanged -> {
                    // 更新进度
                    updateMemoryProgress(event.visibleContent)
                }

                is ReaderEvent.OnStopOrDispose -> {
                    saveToDbImmediate(event.currentContent)
                }

                ReaderEvent.OnPrevChapterClick -> {
                    val currentIndex = uiState.value.progress.chapterIndex
                    if (currentIndex > 0) {
                        switchToChapter(index = currentIndex - 1)
                    } else {
                        logW {
                            "切换章节失败，当前章节为第一章节"
                        }
                        _effects.emit(ReaderEffect.ShowFirstChapterToast)
                        return@launch
                    }
                }

                ReaderEvent.OnNextChapterClick -> {
                    val currentIndex = uiState.value.progress.chapterIndex
                    val chapterSize = uiState.value.chapters.size
                    if (currentIndex < chapterSize - 1) {
                        switchToChapter(index = currentIndex + 1)
                    } else {
                        logW {
                            "切换章节失败，当前章节为最后一章节"
                        }
                        _effects.emit(ReaderEffect.ShowLastChapterToast)
                        return@launch
                    }
                }

            }
        }

    }

    /**
     * 更新内存中的进度
     */
    private fun updateMemoryProgress(content: ReaderContent) {
        viewModelScope.launch {
            // 更新进度
            _uiState.update {
                it.copy(
                    progress = it.progress.copy(
                        chapterIndex = content.chapterIndex,
                        chapterOffsetCharIndex = content.startIndex
                    ),
                    currentContent = content
                )
            }
        }
    }

    /**
     * 计算章节内容的总长度
     */
    private fun calculateChapterLength(items: List<ReaderContent>): Int {
        if (items.isEmpty()) return 1

        // 拿到最后一个内容块
        val lastItem = items.last()

        // 计算它的结束位置
        val lastItemLength = when (lastItem) {
            is ReaderContent.Paragraph -> lastItem.text.length
            is ReaderContent.Title -> lastItem.text.length
            is ReaderContent.Image -> 1 // 图片占 1 个位置
            is ReaderContent.Divider -> 0 // 分割线占 0 个位置
        }

        // 总长度 = 最后一个块的起始位置 + 最后一个块的长度
        return lastItem.startIndex + lastItemLength
    }

    /**
     * 加载指定章节的内容
     */
    private suspend fun loadChapterContent(
        chapterIndex: Int,
        bookFormat: FileFormat,
        bookFileUri: String
    ): List<ReaderContent>? {
        // 获取章节列表
        val chapters = _uiState.value.chapters
        if (chapters.isEmpty()) return null

        // 获取章节
        val chapter = chapters.getOrNull(chapterIndex) ?: return null

        // 解析内容
        return getChapterContentUseCase(bookFileUri, bookFormat, chapter)
            .getOrNull()
            ?.map { block ->
                ContentMapper.mapToUi(chapterIndex, block)
            }
    }


    /**
     * 切换章节
     */
    fun switchToChapter(index: Int) {
        val currentState = _uiState.value
        val book = currentState.book
        val bookId = book.id

        if (book == Book.default || bookId == -1L) {
            logE {
                "切换章节失败，书籍信息缺失"
            }
            return
        }


        viewModelScope.launch(Dispatchers.IO) {
            // 更新数据库进度
            val newProgress = ReadingProgress(
                bookId = bookId,
                chapterIndex = index,
                chapterOffsetCharIndex = 0 // 切换章节默认回到开头
            )

            saveReadingProgressUseCase(bookId, newProgress)
                .onFailure { logE { "进度保存失败" } }

            // 加载最新章节内容
            val newContent = loadChapterContent(
                chapterIndex = index,
                bookFormat = book.format,
                bookFileUri = book.fileUri
            )
            if (newContent != null) {
                val totalLength = calculateChapterLength(newContent)

                // 更新状态
                _uiState.update { state ->
                    state.copy(
                        currentChapterIndex = index,
                        readerItems = newContent, // 替换列表内容
                        progress = newProgress,   // 更新内存中的进度对象
                        currentChapterLength = totalLength
                    )
                }

                // 滚动到顶部
                _effects.emit(ReaderEffect.ScrollToItem(0))

                // 隐藏章节列表
                _effects.emit(ReaderEffect.HideChapterListDrawer)
            } else {
                logE {
                    "加载章节内容失败，索引为 $index"
                }
                _effects.emit(ReaderEffect.ShowLoadChapterContentFailedToast)
                _effects.emit(ReaderEffect.NavigateBack)
            }
        }
    }


    /**
     * 用于退出时保存进度
     */
    private fun saveToDbImmediate(currentContent: ReaderContent?) {
        logI {
            "退出时保存进度"
        }

        val bookId = uiState.value.book.id
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