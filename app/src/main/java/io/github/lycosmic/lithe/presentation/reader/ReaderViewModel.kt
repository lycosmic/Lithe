package io.github.lycosmic.lithe.presentation.reader

import android.graphics.Color
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.ui.text.font.FontFamily
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.lycosmic.data.local.dao.ColorPresetDao
import io.github.lycosmic.data.mapper.toEntity
import io.github.lycosmic.data.settings.AppChapterTitleAlign
import io.github.lycosmic.data.settings.AppImageAlign
import io.github.lycosmic.data.settings.AppTextAlign
import io.github.lycosmic.data.settings.ImageColorEffect
import io.github.lycosmic.data.settings.ProgressTextAlign
import io.github.lycosmic.data.settings.ScreenOrientation
import io.github.lycosmic.data.settings.SettingsManager
import io.github.lycosmic.domain.model.AppFontFamily
import io.github.lycosmic.domain.model.AppFontWeight
import io.github.lycosmic.domain.model.ColorPreset
import io.github.lycosmic.domain.model.FileFormat
import io.github.lycosmic.domain.model.ReadHistory
import io.github.lycosmic.domain.model.ReadingProgress
import io.github.lycosmic.domain.repository.FontFamilyRepository
import io.github.lycosmic.domain.use_case.browse.GetBookUseCase
import io.github.lycosmic.domain.use_case.history.AddHistoryUseCase
import io.github.lycosmic.domain.use_case.reader.GetChapterContentUseCase
import io.github.lycosmic.domain.use_case.reader.GetChapterListUseCase
import io.github.lycosmic.domain.use_case.reader.GetReadingProgressUseCase
import io.github.lycosmic.domain.use_case.reader.SaveReadingProgressUseCase
import io.github.lycosmic.lithe.log.logD
import io.github.lycosmic.lithe.log.logE
import io.github.lycosmic.lithe.log.logI
import io.github.lycosmic.lithe.log.logW
import io.github.lycosmic.lithe.presentation.reader.ReaderEffect.ColorPresetChanged
import io.github.lycosmic.lithe.presentation.reader.ReaderEffect.HideChapterListDrawer
import io.github.lycosmic.lithe.presentation.reader.ReaderEffect.HideTopBarAndBottomControl
import io.github.lycosmic.lithe.presentation.reader.ReaderEffect.NavigateBack
import io.github.lycosmic.lithe.presentation.reader.ReaderEffect.ShowChapterListDrawer
import io.github.lycosmic.lithe.presentation.reader.ReaderEffect.ShowFirstChapterToast
import io.github.lycosmic.lithe.presentation.reader.ReaderEffect.ShowLastChapterToast
import io.github.lycosmic.lithe.presentation.reader.ReaderEffect.ShowOrHideTopBarAndBottomControl
import io.github.lycosmic.lithe.presentation.reader.mapper.ContentMapper
import io.github.lycosmic.lithe.util.AppConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.abs
import kotlin.random.Random

@OptIn(FlowPreview::class)
@HiltViewModel
class ReaderViewModel @Inject constructor(
    private val getChapterListUseCase: GetChapterListUseCase,
    private val getChapterContentUseCase: GetChapterContentUseCase,
    private val getReadingProgressUseCase: GetReadingProgressUseCase,
    private val saveReadingProgressUseCase: SaveReadingProgressUseCase,
    private val getBookUseCase: GetBookUseCase,
    private val settings: SettingsManager,
    private val fontFamilyRepository: FontFamilyRepository,
    private val colorPresetDao: ColorPresetDao,
    private val addHistoryUseCase: AddHistoryUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<ReaderState> = MutableStateFlow(ReaderState())
    val uiState = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<ReaderEffect>()
    val effects = _effects.asSharedFlow()


    // 记录用户拖拽的目标进度
    private var lastUserDragProgress: Float? = null

    // 记录拖拽落点的索引
    private var lastUserDragTargetIndex: Int = -1


    // --- 字体设置 ---
    val fontId = settings.readerFontId.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = AppFontFamily.Default.id
    )

    val fontSize = settings.readerFontSize.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = SettingsManager.DEFAULT_FONT_SIZE
    )

    val fontWeight = settings.appFontWeight.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = AppFontWeight.Normal
    )

    val isItalic = settings.isReaderItalic.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = false
    )

    val letterSpacing = settings.readerLetterSpacing.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = SettingsManager.DEFAULT_LETTER_SPACING
    )

    // --- 文本设置 ---
    val appTextAlign = settings.readerTextAlign.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = AppTextAlign.JUSTIFY
    )

    val lineHeight = settings.readerLineHeight.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = SettingsManager.DEFAULT_LINE_HEIGHT
    )

    val paragraphSpacing = settings.readerParagraphSpacing.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = SettingsManager.DEFAULT_PARAGRAPH_SPACING
    )

    val paragraphIndent = settings.readerParagraphIndent.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = SettingsManager.DEFAULT_PARAGRAPH_INDENT
    )

    // --- 图片设置 ---
    val imageVisible = settings.readerImageEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = true
    )

    val imageCaptionVisible = settings.readerImageCaptionEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = true
    )

    val imageColorEffect = settings.readerImageColorEffect.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = ImageColorEffect.NONE
    )

    val imageCornerRadius = settings.readerImageCornerRadius.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = SettingsManager.DEFAULT_IMAGE_CORNER_RADIUS
    )

    val imageAlign = settings.readerImageAlign.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = AppImageAlign.CENTER
    )

    val imageSizePercent = settings.readerImageSizePercent.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = SettingsManager.DEFAULT_IMAGE_SIZE_PERCENT
    )

    // --- 章节设置 ---
    val chapterTitleAlign = settings.readerChapterTitleAlign.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = AppChapterTitleAlign.CENTER
    )

    // --- 边距设置 ---
    val sidePadding = settings.readerSidePadding.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = 1
    )

    val verticalPadding = settings.readerVerticalPadding.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = 0
    )

    val cutoutPaddingApply = settings.readerCutoutPaddingApply.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = true
    )

    val bottomBarPadding = settings.readerBottomBarPadding.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = 0
    )

    // --- 系统设置 ---
    val isCustomBrightness = settings.readerCustomBrightnessEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = false
    )

    val customBrightnessValue = settings.readerCustomBrightness.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = 0.5f
    )

    val screenOrientation = settings.screenOrientation.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = ScreenOrientation.DEFAULT
    )

    // --- 进度设置 ---
    val isBottomProgressTextVisible = settings.showProgressText.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = true
    )

    val progressTextSize = settings.progressBarFontSize.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = 16
    )

    val bottomProgressPadding = settings.progressBarMargin.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = 0
    )

    val bottomProgressTextAlign = settings.progressBarTextAlign.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = ProgressTextAlign.CENTER
    )

    // --- 杂项设置 ---
    val isFullScreen = settings.isFullScreenEnabled.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = true
    )

    val screenAlive = settings.keepScreenOn.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = true
    )

    val isHideBarOnQuickScroll = settings.hideBarWhenQuickScroll.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = true
    )

    // --- 颜色预设设置 ---
    /**
     * 数据库中的颜色预设列表
     */
    private val presetsFlow = colorPresetDao.getAllPresets()

    /**
     * 当前选中的颜色预设 ID
     */
    private val selectedIdFlow = settings.currentColorPresetId

    /**
     * 颜色预设列表
     */
    val colorPresets = selectedIdFlow.combine(presetsFlow) { selectedId, presets ->
        presets.sortedBy { it.sortOrder }.map { entity ->
            ColorPreset(
                id = entity.id ?: 0,
                name = entity.name,
                isSelected = entity.id == selectedId,
                backgroundColorArgb = entity.backgroundColorArgb,
                textColorArgb = entity.textColorArgb
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = emptyList()
    )

    /**
     * 当前选中的颜色预设
     */
    val currentColorPreset = colorPresets
        .mapNotNull { list ->
            list.find { it.isSelected }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
            initialValue = ColorPreset.default
        )

    /**
     * 快速更改颜色预设
     */
    val isQuickColorPresetChangeEnabled = settings.quickChangeColorPreset.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
        initialValue = false
    )

    /**
     * 可使用的字体列表
     */
    val availableFonts = fontFamilyRepository.getAvailableFonts()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
            emptyList()
        )

    val fontFamily =
        combine(fontId, fontWeight, isItalic) { id, fontWeight, isItalic ->
            fontFamilyRepository.getFontFamily(id, fontWeight, isItalic) as FontFamily
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(AppConstants.STATE_FLOW_STOP_TIMEOUT),
            initialValue = FontFamily.Default
        )


    // 数据库保存的流
    private val _saveProgressFlow = MutableSharedFlow<Unit>(
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
                _effects.emit(NavigateBack)
                return@launch
            }

            _uiState.update {
                it.copy(
                    book = book,
                )
            }

            // 新增阅读历史
            addHistoryUseCase(
                ReadHistory(
                    book = book,
                    time = System.currentTimeMillis()
                )
            )

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
                val targetItemIndex = _uiState.value.progress.uiItemIndex
                val targetItemOffset = _uiState.value.progress.uiItemOffset
                _effects.emit(ReaderEffect.ScrollToItem(targetItemIndex, targetItemOffset))

                // 滚动到当前章节
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
                .collect { _ ->
                    saveToDb()
                }
        }
    }

    fun onEvent(event: ReaderEvent) {
        viewModelScope.launch {
            when (event) {
                ReaderEvent.OnReadContentClick -> {
                    _effects.emit(ShowOrHideTopBarAndBottomControl)
                }

                ReaderEvent.OnBackClick -> {
                    _effects.emit(NavigateBack)
                }

                ReaderEvent.OnChapterMenuClick -> {
                    _effects.emit(ShowChapterListDrawer)
                }

                ReaderEvent.OnDrawerBackClick -> {
                    _effects.emit(HideChapterListDrawer)
                }

                is ReaderEvent.OnChapterItemClick -> {
                    switchToChapter(index = event.index)
                }

                is ReaderEvent.OnContentListStateChange -> {
                    // 更新进度
                    updateMemoryProgress(event.listState)
                }

                is ReaderEvent.OnStopOrDispose -> {
                    saveToDbImmediate()
                }

                ReaderEvent.OnPrevChapterClick -> {
                    val currentIndex = uiState.value.progress.chapterIndex
                    if (currentIndex > 0) {
                        switchToChapter(index = currentIndex - 1)
                    } else {
                        logW {
                            "切换章节失败，当前章节为第一章节"
                        }
                        _effects.emit(ShowFirstChapterToast)
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
                        _effects.emit(ShowLastChapterToast)
                        return@launch
                    }
                }

                // --- 设置相关事件处理 ---

                // 字体设置
                is ReaderEvent.OnFontIdChange -> {
                    settings.setReaderFontId(event.fontId)
                }

                is ReaderEvent.OnFontSizeChange -> {
                    settings.setReaderFontSize(event.fontSize)
                }

                is ReaderEvent.OnFontWeightChange -> {
                    settings.setReaderFontWeight(event.fontWeight)
                }

                is ReaderEvent.OnItalicChange -> {
                    settings.setReaderItalic(event.isItalic)
                }

                is ReaderEvent.OnLetterSpacingChange -> {
                    settings.setReaderLetterSpacing(event.letterSpacing)
                }

                // 文本设置
                is ReaderEvent.OnTextAlignChange -> {
                    settings.setReaderTextAlign(event.textAlign)
                }

                is ReaderEvent.OnLineHeightChange -> {
                    settings.setReaderLineHeight(event.lineHeight)
                }

                is ReaderEvent.OnParagraphSpacingChange -> {
                    settings.setReaderParagraphSpacing(event.paragraphSpacing)
                }

                is ReaderEvent.OnParagraphIndentChange -> {
                    settings.setReaderParagraphIndent(event.paragraphIndent)
                }

                // 图片设置
                is ReaderEvent.OnImageVisibleChange -> {
                    settings.setReaderImageEnabled(event.isVisible)
                }

                is ReaderEvent.OnImageCaptionVisibleChange -> {
                    settings.setReaderImageCaptionEnabled(event.isVisible)
                }

                is ReaderEvent.OnImageColorEffectChange -> {
                    settings.setReaderImageColorEffect(event.effect)
                }

                is ReaderEvent.OnImageCornerRadiusChange -> {
                    settings.setReaderImageCornerRadius(event.radius)
                }

                is ReaderEvent.OnImageAlignChange -> {
                    settings.setReaderImageAlign(event.align)
                }

                is ReaderEvent.OnImageSizePercentChange -> {
                    settings.setReaderImageSizePercent(event.percent)
                }

                // 章节设置
                is ReaderEvent.OnChapterTitleAlignChange -> {
                    settings.setReaderChapterTitleAlign(event.align)
                }

                // 边距设置
                is ReaderEvent.OnPageTurnModeChange -> {
                    settings.setReaderMode(event.mode)
                }

                is ReaderEvent.OnSidePaddingChange -> {
                    settings.setReaderSidePadding(event.padding)
                }

                is ReaderEvent.OnVerticalPaddingChange -> {
                    settings.setReaderVerticalPadding(event.padding)
                }

                is ReaderEvent.OnCutoutPaddingApplyChange -> {
                    settings.setReaderCutoutPaddingApply(event.isApply)
                }

                is ReaderEvent.OnBottomMarginChange -> {
                    settings.setReaderBottomMargin(event.margin)
                }

                // 进度设置
                is ReaderEvent.OnProgressBarVisibleChange -> {
                    settings.setShowProgressBar(event.isVisible)
                }

                is ReaderEvent.OnProgressBarFontSizeChange -> {
                    settings.setProgressBarFontSize(event.size)
                }

                is ReaderEvent.OnProgressBarMarginChange -> {
                    settings.setProgressBarMargin(event.margin)
                }

                is ReaderEvent.OnProgressBarTextAlignChange -> {
                    settings.setProgressBarTextAlign(event.align)
                }

                // 颜色预设设置
                is ReaderEvent.OnColorPresetClick -> {
                    settings.setCurrentColorPresetId(event.preset.id)
                }

                is ReaderEvent.OnAddColorPresetClick -> {
                    // 新增颜色预设
                    val maxSortOrder = colorPresetDao.getMaxSortOrder()
                    colorPresetDao.insertPreset(
                        ColorPreset.default.copy(
                            isSelected = true
                        ).toEntity(
                            maxSortOrder?.plus(1) ?: 0
                        )
                    )
                }

                is ReaderEvent.OnDeleteColorPresetClick -> {
                    if (colorPresets.value.size <= 1) {
                        logW {
                            "[删除颜色预设] 颜色预设列表至少需要有一个颜色预设，无法删除"
                        }
                        return@launch
                    }

                    val preset = colorPresets.value.find { it.id == event.preset.id } ?: run {
                        logE {
                            "[删除颜色预设] 当前要删除的颜色预设不存在，id: ${event.preset.id}"
                        }
                        return@launch
                    }

                    val nextIndex =
                        if (colorPresets.value.lastIndex == colorPresets.value.indexOf(preset)) {
                            colorPresets.value.lastIndex - 1
                        } else {
                            colorPresets.value.indexOf(preset) + 1
                        }

                    colorPresetDao.deletePreset(preset.toEntity(-1))
                    // 选择下一个颜色预设
                    val nextPreset = colorPresets.value[nextIndex]
                    settings.setCurrentColorPresetId(nextPreset.id)
                }

                is ReaderEvent.OnShuffleColorPresetClick -> {
                    // 打乱颜色预设的颜色值
                    val preset = colorPresets.value.find { it.id == event.preset.id } ?: run {
                        logE {
                            "[打乱颜色预设的颜色值] 当前要打乱的颜色预设不存在，id: ${event.preset.id}"
                        }
                        return@launch
                    }

                    colorPresetDao.updatePreset(
                        preset.copy(
                            backgroundColorArgb = Color.rgb(
                                Random.nextInt(256),
                                Random.nextInt(256),
                                Random.nextInt(256)
                            ),
                            textColorArgb = Color.rgb(
                                Random.nextInt(256),
                                Random.nextInt(256),
                                Random.nextInt(256)
                            )
                        ).toEntity(
                            getPresetSortOrder(preset)
                        )
                    )
                }

                is ReaderEvent.OnColorPresetNameChange -> {
                    // 修改颜色预设名称
                    val preset = colorPresets.value.find { it.id == event.preset.id } ?: run {
                        logE {
                            "[修改颜色预设的名称] 当前要修改的颜色预设不存在，id: ${event.preset.id}"
                        }
                        return@launch
                    }
                    colorPresetDao.updatePreset(
                        preset.copy(
                            name = event.name
                        ).toEntity(
                            getPresetSortOrder(preset)
                        )
                    )
                }

                is ReaderEvent.OnColorPresetBgColorChange -> {
                    val preset = colorPresets.value.find { it.id == event.preset.id } ?: run {
                        logE {
                            "[修改颜色预设的背景颜色] 当前要修改的颜色预设不存在，id: ${event.preset.id}"
                        }
                        return@launch
                    }
                    colorPresetDao.updatePreset(
                        preset.copy(backgroundColorArgb = event.colorArgb).toEntity(
                            getPresetSortOrder(preset)
                        )
                    )
                }

                is ReaderEvent.OnColorPresetTextColorChange -> {
                    val preset = colorPresets.value.find { it.id == event.preset.id } ?: run {
                        logE {
                            "[修改颜色预设的文本颜色] 当前要修改的颜色预设不存在，id: ${event.preset.id}"
                        }
                        return@launch
                    }
                    colorPresetDao.updatePreset(
                        preset.copy(textColorArgb = event.colorArgb).toEntity(
                            getPresetSortOrder(preset)
                        )
                    )
                }

                is ReaderEvent.OnColorPresetDragStopped -> {
                    // 拖拽结束，更新排序
                    logI {
                        "拖拽结束，更新数据库中颜色预设的排序"
                    }

                    val newPresets = event.presets.mapIndexed { index, preset ->
                        preset.toEntity(index)
                    }
                    logD {
                        "新排序为：${newPresets.map { it.name }}"
                    }
                    colorPresetDao.updatePresets(newPresets)
                }

                is ReaderEvent.OnQuickChangeColorPresetEnabledChange -> {
                    settings.setQuickChangeColorPresetEnabled(event.enabled)
                }

                // 系统设置
                is ReaderEvent.OnCustomBrightnessEnabledChange -> {
                    settings.setReaderCustomBrightnessEnabled(event.isEnabled)
                }

                is ReaderEvent.OnCustomBrightnessValueChange -> {
                    settings.setReaderCustomBrightness(event.value)
                }

                is ReaderEvent.OnScreenOrientationChange -> {
                    settings.setScreenOrientation(event.orientation)
                }

                // 杂项设置
                is ReaderEvent.OnIsFullScreenChange -> {
                    settings.setIsFullScreenEnabled(event.isFullScreen)
                }

                is ReaderEvent.OnIsKeepScreenOnChange -> {
                    settings.setKeepScreenOn(event.isKeep)
                }

                is ReaderEvent.OnIsHideBarWhenQuickScrollChange -> {
                    settings.setHideBarWhenQuickScroll(event.isHide)
                }

                ReaderEvent.OnSwipeLastColorPreset -> {
                    colorPresets.value.run {
                        if (isEmpty()) {
                            logE {
                                "当前没有颜色预设，无法切换"
                            }
                            return@run
                        }

                        if (size == 1) {
                            logW {
                                "当前只有一个颜色预设，无法切换"
                            }
                            return@run
                        }

                        // 获取当前选中的颜色预设
                        val currentPreset = find { it.id == currentColorPreset.value.id }
                            ?: run {
                                logE {
                                    "[获取当前颜色预设] 当前颜色预设不存在，id: ${currentColorPreset.value.id}"
                                }

                                return@launch
                            }

                        // 获取上一个颜色预设
                        val previousPreset =
                            getOrElse(indexOf(currentPreset) - 1) {
                                get(size - 1)
                            }

                        // 切换当前颜色预设
                        settings.setCurrentColorPresetId(previousPreset.id)

                        // 已选择 《预设名称》
                        _effects.emit(ColorPresetChanged(previousPreset))
                    }
                }

                ReaderEvent.OnSwipeNextColorPreset -> {
                    colorPresets.value.run {
                        if (isEmpty()) {
                            logE {
                                "当前没有颜色预设，无法切换"
                            }
                            return@run
                        }

                        if (size == 1) {
                            logW {
                                "当前只有一个颜色预设，无法切换"
                            }
                            return@run
                        }

                        // 获取当前选中的颜色预设
                        val currentPreset = find { it.id == currentColorPreset.value.id }
                            ?: run {
                                logE {
                                    "[获取当前颜色预设] 当前颜色预设不存在，id: ${currentColorPreset.value.id}"
                                }

                                return@launch
                            }

                        // 获取下一个颜色预设
                        val nextPreset =
                            getOrElse(indexOf(currentPreset) + 1) {
                                get(0)
                            }

                        // 切换当前颜色预设
                        settings.setCurrentColorPresetId(nextPreset.id)
                        _effects.emit(ColorPresetChanged(nextPreset))

                    }
                }

                is ReaderEvent.OnBarsVisibleChange -> {
                    _effects.emit(HideTopBarAndBottomControl)
                }

                is ReaderEvent.OnBookProgressChange -> {
                    val progress = event.progress // 全书比例
                    val chapters = _uiState.value.chapters

                    logD {
                        "用户拖拽进度为：$progress"
                    }

                    // 1. 计算全书有效总字节数
                    val totalEffectiveBytes = chapters.sumOf { it.length }.toDouble()
                    // 2. 计算目标有效全局字节位置
                    val targetGlobalEffectiveBytes = (totalEffectiveBytes * progress).toLong()

                    var accumulatedEffectiveBytes = 0L
                    var targetChapterIndex = 0
                    var targetEffectiveOffsetInChapter = 0L

                    // 3. 找出进度落在哪个章节
                    for ((index, chapter) in chapters.withIndex()) {
                        if (accumulatedEffectiveBytes + chapter.length > targetGlobalEffectiveBytes) {
                            targetChapterIndex = index
                            // 目标章节内的有效偏移量 = 目标全局 - 当前章节前的积累
                            targetEffectiveOffsetInChapter =
                                targetGlobalEffectiveBytes - accumulatedEffectiveBytes
                            break
                        }
                        accumulatedEffectiveBytes += chapter.length
                    }

                    // 4. 执行跳转
                    switchToChapter(
                        index = targetChapterIndex,
                        targetEffectiveOffsetInChapter = targetEffectiveOffsetInChapter,
                        expectedGlobalProgress = progress // 用于防止进度条回跳
                    )
                }
            }
        }
    }

    /**
     * 获取当前颜色预设的排序
     */
    private suspend fun getPresetSortOrder(preset: ColorPreset): Int {
        return colorPresetDao.getPresetSortOrderById(preset.id)
            ?: colorPresetDao.getMaxSortOrder() ?: 0
    }

    /**
     * 更新内存中的进度
     */
    private fun updateMemoryProgress(listState: LazyListState) {
        viewModelScope.launch {
            var chapterProgress: Float
            var uiIndex: Int
            var uiOffset: Int

            run {
                val layoutInfo = listState.layoutInfo
                val totalItems = layoutInfo.totalItemsCount
                if (totalItems == 0) {
                    chapterProgress = 0f
                    uiIndex = 0
                    uiOffset = 0
                    return@run
                }

                val visibleItemsInfo = layoutInfo.visibleItemsInfo
                if (visibleItemsInfo.isEmpty()) {
                    chapterProgress = 0f
                    uiIndex = 0
                    uiOffset = 0
                    return@run
                }

                // 获取第一个可见项的信息
                val firstIndex = listState.firstVisibleItemIndex
                val firstOffset = listState.firstVisibleItemScrollOffset
                if (firstIndex >= _uiState.value.readerItems.size) {
                    chapterProgress = 0f
                    uiIndex = 0
                    uiOffset = 0
                    return@run
                }
                val firstVisibleContent = _uiState.value.readerItems[firstIndex]


                // 获取最后一个可见项的信息
                val lastVisibleItem = visibleItemsInfo.last()
                val isLastItemVisible = lastVisibleItem.index == totalItems - 1
                // 如果是最后一个数据 AND 底部已经进入屏幕范围内
                val isAtEnd = isLastItemVisible
                        && (lastVisibleItem.offset + lastVisibleItem.size <= layoutInfo.viewportEndOffset) // 屏幕可视区域结束位置

                if (isAtEnd) {
                    chapterProgress = 1f
                    uiIndex = firstIndex
                    uiOffset = firstOffset
                    // 更新章节进度
                    _uiState.update {
                        it.copy(
                            chapterProgress = chapterProgress,
                            progress = it.progress.copy(
                                chapterIndex = firstVisibleContent.chapterIndex,
                                chapterOffsetCharIndex = firstVisibleContent.startIndex,
                                uiItemIndex = uiIndex,
                                uiItemOffset = uiOffset,
                                lastReadTime = System.currentTimeMillis(),
                            ),
                        )
                    }
                    return@run
                }


                // 章节进度公式：(第一个可见内容项的起始字符索引) / 当前章节字符长度
                val rawProgress =
                    (firstVisibleContent.startIndex).toFloat() / (_uiState.value.currentChapterLength).toFloat()

                chapterProgress = rawProgress.coerceIn(0f, 1f)
                uiIndex = firstIndex
                uiOffset = firstOffset

                // 更新章节进度
                _uiState.update {
                    it.copy(
                        chapterProgress = chapterProgress,
                        progress = it.progress.copy(
                            chapterIndex = firstVisibleContent.chapterIndex,
                            chapterOffsetCharIndex = firstVisibleContent.startIndex,
                            uiItemIndex = uiIndex,
                            uiItemOffset = uiOffset,
                            lastReadTime = System.currentTimeMillis(),
                        ),
                    )
                }
            }

            run {
                val chapterIndex = _uiState.value.currentChapterIndex
                // 全书进度应用“粘性逻辑”
                val progressPercent =
                    if (lastUserDragProgress != null &&
                        lastUserDragTargetIndex != -1 &&
                        abs(listState.firstVisibleItemIndex - lastUserDragTargetIndex) <= 1
                    ) {
                        // 如果当前还在用户拖拽落点的附近，强制显示用户拖拽的值，抑制了自动回跳
                        lastUserDragProgress!!
                    } else {
                        // 用户已经手动划走了一定距离，说明他在认真阅读了
                        // 此时解除锁定，恢复真实的数学计算
                        if (lastUserDragProgress != null) {
                            lastUserDragProgress = null
                            lastUserDragTargetIndex = -1
                        }
                        calculateBookProgress(chapterProgress, chapterIndex)
                    }

                _uiState.update {
                    it.copy(
                        progress = it.progress.copy(
                            progressPercent = progressPercent,
                            lastReadTime = System.currentTimeMillis(),
                        ),
                    )
                }
            }

        }
    }

    /**
     * 获取全书进度
     * 全书进度公式：（之前章节的字节大小 + 当前章节字节大小 * 当前章节阅读进度） / 所有章节字节大小
     * @param chapterIndex 当前章节索引
     * @param chapterProgress 当前章节进度
     */
    private fun calculateBookProgress(chapterProgress: Float, chapterIndex: Int): Float {
        val chapterList = _uiState.value.chapters

        // 当前章节索引
        val currentChapterIndex = chapterIndex

        // 之前章节的字节大小
        val previousChaptersLength = chapterList.take(currentChapterIndex).sumOf {
            it.length
        }

        // 当前章节的字节大小 * 当前章节阅读进度
        val currentChapterLength = _uiState.value.currentChapterLength * chapterProgress

        // 所有章节字节大小
        val allChaptersLength = chapterList.sumOf {
            it.length
        }.coerceAtLeast(1)

        val progressPercent =
            (previousChaptersLength + currentChapterLength) / allChaptersLength.toFloat()

        return progressPercent
    }

    /**
     * 获取内容块的长度
     */
    private fun getReaderItemLength(item: ReaderContent): Int {
        return when (item) {
            is ReaderContent.Paragraph -> item.text.length
            is ReaderContent.Title -> item.text.length
            is ReaderContent.Image -> 1 // 图片占 1 个位置
            is ReaderContent.Divider -> 0 // 分割线占 0 个位置
            is ReaderContent.ImageDescription -> 0
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
        val lastItemLength = getReaderItemLength(lastItem)
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
        val chapters = uiState.value.chapters
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
     * @param index 目标章节索引
     * @param targetEffectiveOffsetInChapter 目标在该章节内部的【有效】字节偏移量
     * @param expectedGlobalProgress 用户拖拽的原始进度
     */
    fun switchToChapter(
        index: Int,
        targetEffectiveOffsetInChapter: Long = 0L,
        expectedGlobalProgress: Float? = null
    ) {
        val book = _uiState.value.book
        val chapter = _uiState.value.chapters.getOrNull(index) ?: return

        // 更新章节索引
        _uiState.update {
            it.copy(currentChapterIndex = index)
        }

        viewModelScope.launch(Dispatchers.IO) {
            // 1. 加载章节内容
            val newContent = loadChapterContent(
                chapterIndex = index,
                bookFormat = book.format,
                bookFileUri = book.fileUri
            )

            if (newContent == null) {
                _effects.emit(ReaderEffect.ShowLoadChapterContentFailedToast)
                return@launch
            }

            // 2. 计算内容的字符总长度
            var parsedContentTotalLength = 0
            for (item in newContent) {
                parsedContentTotalLength += getReaderItemLength(item)
            }

            // 3. 计算章节内部的进度比例
            val chapterProgressRatio = if (chapter.length > 0) {
                (targetEffectiveOffsetInChapter.toDouble() / chapter.length)
                    .coerceIn(0.0, 1.0)
            } else 0.0

            // 4. 根据比例，映射到解析后的字符位置
            val targetParsedCharPosition = (parsedContentTotalLength * chapterProgressRatio).toInt()

            // 5. 遍历寻找对应的索引
            var targetItemIndex = 0
            var currentAccumulatedLength = 0
            for (i in newContent.indices) {
                val itemLength = getReaderItemLength(newContent[i])
                if (currentAccumulatedLength + itemLength > targetParsedCharPosition) {
                    targetItemIndex = i
                    break
                }
                currentAccumulatedLength += itemLength
            }

            // 保存用户的意图作为锚点
            if (expectedGlobalProgress != null) {
                lastUserDragProgress = expectedGlobalProgress
                lastUserDragTargetIndex = targetItemIndex // 记录跳转落点的索引
            } else {
                // 如果不是拖拽，则清除锚点
                lastUserDragProgress = null
                lastUserDragTargetIndex = -1
            }

            // 6. 计算最终的显示进度
            // 如果是用户拖拽，强制使用用户的值，防止进度条“跳变”
            // 否则重新计算
            val finalGlobalProgress = expectedGlobalProgress ?: calculateBookProgress(
                chapterProgress = chapterProgressRatio.toFloat(),
                chapterIndex = index
            )

            // 7. 更新状态和数据库
            val newProgress = ReadingProgress(
                bookId = book.id,
                chapterIndex = index,
                chapterOffsetCharIndex = targetParsedCharPosition, // 存的是真实的字符位置
                progressPercent = finalGlobalProgress            // 存的是计算后的比例
            )

            saveReadingProgressUseCase(book.id, newProgress)

            _uiState.update { state ->
                state.copy(
                    currentChapterIndex = index,
                    chapterProgress = chapterProgressRatio.toFloat(),
                    readerItems = newContent,
                    currentChapterLength = parsedContentTotalLength,
                    progress = newProgress
                )
            }

            // 8. 触发滚动效果
            _effects.emit(ReaderEffect.ScrollToItem(targetItemIndex, 0))

            // 9. 隐藏菜单
            _effects.emit(HideChapterListDrawer)
        }
    }


    /**
     * 用于退出时保存进度
     */
    private fun saveToDbImmediate() {
        logI {
            "退出时保存进度"
        }

        saveToDb()
    }


    /**
     * 数据库保存进度
     */
    private fun saveToDb() {
        viewModelScope.launch(Dispatchers.IO) {
            val state = _uiState.value
            saveReadingProgressUseCase(
                bookId = state.book.id,
                progress = state.progress
            ).onFailure {
                // 保存失败
                logE {
                    "保存进度失败"
                }
                return@launch
            }

            logD {
                "保存进度成功，进度：${state.progress}"
            }
        }
    }
}