package io.github.lycosmic.lithe.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.lycosmic.domain.use_case.history.DeleteAllHistoriesUseCase
import io.github.lycosmic.domain.use_case.history.DeleteHistoryUseCase
import io.github.lycosmic.domain.use_case.history.GetHistoriesUseCase
import io.github.lycosmic.lithe.presentation.history.HistoryEffect.NavigateToBookDetail
import io.github.lycosmic.lithe.presentation.history.HistoryEffect.NavigateToBookReading
import io.github.lycosmic.lithe.presentation.history.HistoryEffect.OnDeleteWholeHistoryDialogDismiss
import io.github.lycosmic.lithe.presentation.history.HistoryEffect.OnDeleteWholeHistoryDialogShow
import io.github.lycosmic.lithe.presentation.history.HistoryEffect.OnMoreBottomSheetDismiss
import io.github.lycosmic.lithe.presentation.history.HistoryEffect.OnMoreBottomSheetShow
import io.github.lycosmic.lithe.presentation.history.cpmponents.HistoryTopBarState
import io.github.lycosmic.lithe.presentation.history.model.GroupedHistory
import io.github.lycosmic.lithe.presentation.history.model.HistoryTimeLabel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getHistoriesUseCase: GetHistoriesUseCase,
    private val deleteAllHistoriesUseCase: DeleteAllHistoriesUseCase,
    private val deleteHistoryUseCase: DeleteHistoryUseCase
) : ViewModel() {
    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    private val _topBarState: MutableStateFlow<HistoryTopBarState> =
        MutableStateFlow(HistoryTopBarState.Default)
    val topBarState = _topBarState.asStateFlow()

    private val _groupedHistories = MutableStateFlow<List<GroupedHistory>>(emptyList())
    val groupedHistories = _groupedHistories.asStateFlow()


    private val _effects = MutableSharedFlow<HistoryEffect>()
    val effects = _effects.asSharedFlow()

    init {
        observeHistories()
    }


    /**
     * 处理事件
     */
    fun onEvent(event: HistoryEvent) {
        viewModelScope.launch {
            when (event) {
                HistoryEvent.OnDeleteWholeHistoryClick -> {
                    _effects.emit(OnDeleteWholeHistoryDialogShow)
                }

                HistoryEvent.OnExitSearchModeClick -> {
                    _topBarState.update {
                        HistoryTopBarState.Default
                    }
                }

                is HistoryEvent.OnHistoryTitleClick -> {
                    // 跳转到阅读页
                    _effects.emit(NavigateToBookReading(event.bookId))
                }

                HistoryEvent.OnSearchClick -> {
                    _topBarState.update {
                        HistoryTopBarState.Search
                    }
                }

                HistoryEvent.OnAboutClick -> {
                    _effects.emit(OnMoreBottomSheetDismiss)
                }

                HistoryEvent.OnDeleteWholeHistoryDialogConfirm -> {
                    deleteAllHistoriesUseCase()
                    _effects.emit(OnDeleteWholeHistoryDialogDismiss)
                }

                HistoryEvent.OnDeleteWholeHistoryDialogDismiss -> {
                    _effects.emit(OnDeleteWholeHistoryDialogDismiss)
                }

                HistoryEvent.OnHelpClick -> {
                    _effects.emit(OnMoreBottomSheetDismiss)
                }

                HistoryEvent.OnMoreBottomSheetDismiss -> {
                    _effects.emit(OnMoreBottomSheetDismiss)
                }

                HistoryEvent.OnMoreClick -> {
                    _effects.emit(OnMoreBottomSheetShow)
                }

                is HistoryEvent.OnSearchTextChange -> {
                    _searchText.update {
                        event.text
                    }
                }

                HistoryEvent.OnSettingsClick -> {
                    _effects.emit(HistoryEffect.NavigateToSettings)
                    _effects.emit(OnMoreBottomSheetDismiss)
                }

                is HistoryEvent.OnDeleteHistoryClick -> {
                    // 删除该阅读记录
                    deleteHistoryUseCase(event.history)
                }

                is HistoryEvent.OnHistoryClick -> {
                    // 跳转到书籍详情页
                    _effects.emit(NavigateToBookDetail(event.bookId))
                }
            }
        }
    }


    /**
     * 观察阅读历史记录
     */
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    fun observeHistories() {
        /**
         * 获取日期标签
         */
        fun getDayLabel(timeMillis: Long): HistoryTimeLabel {
            val historyDate = Instant.ofEpochMilli(timeMillis)
                .atZone(ZoneId.systemDefault()).toLocalDate()
            val today = LocalDate.now()
            val yesterday = today.minusDays(1)

            return when (historyDate.dayOfYear) {
                today.dayOfYear -> HistoryTimeLabel.Today
                yesterday.dayOfYear -> HistoryTimeLabel.Yesterday
                else -> HistoryTimeLabel.Other(historyDate.format(DateTimeFormatter.ofPattern("yy.MM.dd")))
            }
        }

        viewModelScope.launch {
            // 监听搜索文本的变化
            _searchText
                .debounce(100)
                .distinctUntilChanged()
                .flatMapLatest { searchText ->
                    // 每次搜索文本变化时重新查询
                    getHistoriesUseCase(searchText)
                        .map { histories ->
                            histories
                                .groupBy { history ->
                                    // 根据时间分组
                                    getDayLabel(history.time)
                                }.map { (dayLabel, histories) ->
                                    // 只保留同一本书最新的阅读记录
                                    // 1. 按书籍ID分组
                                    val groupedByBookId = histories.groupBy { history ->
                                        history.book.id
                                    }

                                    // 2. 找到时间最晚的记录
                                    val latestRecords =
                                        groupedByBookId.values.mapNotNull { bookHistories ->
                                            bookHistories.maxByOrNull { history ->
                                                history.time
                                            }
                                        }

                                    // 3. 构建结果
                                    GroupedHistory(dayLabel, latestRecords)
                                }
                        }

                }
                .collect { groupedHistories ->
                    _groupedHistories.update {
                        groupedHistories
                    }
                }
        }
    }
}