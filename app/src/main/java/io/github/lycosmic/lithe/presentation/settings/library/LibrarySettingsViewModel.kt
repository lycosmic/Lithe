package io.github.lycosmic.lithe.presentation.settings.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.lycosmic.lithe.data.local.entity.CategoryEntity
import io.github.lycosmic.lithe.data.model.BookTitlePosition
import io.github.lycosmic.lithe.data.model.BrowseDisplayMode
import io.github.lycosmic.lithe.data.model.Constants
import io.github.lycosmic.lithe.data.repository.CategoryRepository
import io.github.lycosmic.lithe.data.settings.SettingsManager
import io.github.lycosmic.lithe.extension.logI
import io.github.lycosmic.lithe.extension.logW
import io.github.lycosmic.lithe.presentation.settings.library.LibrarySettingsEffect.CategoryNameExists
import io.github.lycosmic.lithe.presentation.settings.library.LibrarySettingsEffect.OpenDeleteCategoryDialog
import io.github.lycosmic.lithe.presentation.settings.library.LibrarySettingsEffect.OpenEditCategoryDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibrarySettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    /**
     * 书籍显示模式
     */
    val bookDisplayMode = settingsManager.bookDisplayMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(Constants.STATE_FLOW_STOP_TIMEOUT_MILLIS),
        initialValue = BrowseDisplayMode.List
    )

    /**
     * 书籍网格大小
     */
    val bookGridColumnCount = settingsManager.bookGridColumnCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(Constants.STATE_FLOW_STOP_TIMEOUT_MILLIS),
        initialValue = Constants.GRID_SIZE_INT_RANGE.first
    )

    /**
     * 书籍标题位置
     */
    val bookTitlePosition = settingsManager.bookTitlePosition.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(Constants.STATE_FLOW_STOP_TIMEOUT_MILLIS),
        initialValue = BookTitlePosition.Below
    )

    /**
     * 显示阅读按钮
     */
    val showReadButton = settingsManager.showReadButton.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(Constants.STATE_FLOW_STOP_TIMEOUT_MILLIS),
        initialValue = true
    )

    /**
     * 显示阅读进度
     */
    val showReadProgress = settingsManager.showReadProgress.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(Constants.STATE_FLOW_STOP_TIMEOUT_MILLIS),
        initialValue = true
    )

    /**
     * 显示分类标签页
     */
    val showCategoryTab = settingsManager.showCategoryTab.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(Constants.STATE_FLOW_STOP_TIMEOUT_MILLIS),
        initialValue = true
    )

    /**
     * 总是显示默认分类标签页
     */
    val alwaysShowDefaultCategoryTab = settingsManager.alwaysShowDefaultCategoryTab.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(Constants.STATE_FLOW_STOP_TIMEOUT_MILLIS),
        initialValue = true
    )

    /**
     * 显示书籍数量
     */
    val showBookCount = settingsManager.showBookCount.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(Constants.STATE_FLOW_STOP_TIMEOUT_MILLIS),
        initialValue = true
    )

    /**
     * 每个分类有不同的排序方式
     */
    val eachCategoryHasDifferentSort = settingsManager.eachCategoryHasDifferentSort.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(Constants.STATE_FLOW_STOP_TIMEOUT_MILLIS),
        initialValue = true
    )

    // 当前的分类列表
    val categoryList = categoryRepository.getCategories().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(Constants.STATE_FLOW_STOP_TIMEOUT_MILLIS),
        initialValue = emptyList()
    )


    private val _effects = MutableSharedFlow<LibrarySettingsEffect>()
    val effects = _effects.asSharedFlow()


    fun onEvent(event: LibrarySettingsEvent) {
        when (event) {
            LibrarySettingsEvent.OnBackClick -> {
                viewModelScope.launch(Dispatchers.Default) {
                    _effects.emit(LibrarySettingsEffect.NavigateBack)
                }
            }

            LibrarySettingsEvent.OnCreateCategoryClick -> {
                // 打开创建分类对话框
                viewModelScope.launch(Dispatchers.Default) {
                    _effects.emit(LibrarySettingsEffect.OpenCreateCategoryDialog)
                }
            }

            is LibrarySettingsEvent.OnBookDisplayModeChange -> {
                viewModelScope.launch(Dispatchers.IO) {
                    settingsManager.setBookDisplayMode(event.libraryDisplayMode)
                }
            }

            is LibrarySettingsEvent.OnBookGridColumnCountChange -> {
                viewModelScope.launch(Dispatchers.IO) {
                    settingsManager.setBookGridColumnCount(event.columnCount)
                }
            }

            is LibrarySettingsEvent.OnBookShowProgressChange -> {
                viewModelScope.launch(Dispatchers.IO) {
                    settingsManager.setShowReadProgress(event.showProgress)
                }
            }

            is LibrarySettingsEvent.OnBookShowReadButtonChange -> {
                viewModelScope.launch(Dispatchers.IO) {
                    settingsManager.setShowReadButton(event.showReadButton)
                }
            }

            is LibrarySettingsEvent.OnBookTitlePositionChange -> {
                viewModelScope.launch(Dispatchers.IO) {
                    settingsManager.setBookTitlePosition(event.bookTitlePosition)
                }
            }

            is LibrarySettingsEvent.OnCategoryBookCountVisibleChange -> {
                viewModelScope.launch(Dispatchers.IO) {
                    settingsManager.setShowBookCount(event.visible)
                }
            }

            is LibrarySettingsEvent.OnCategorySortDifferentChange -> {
                viewModelScope.launch(Dispatchers.IO) {
                    settingsManager.setEachCategoryHasDifferentSort(event.sortDifferent)
                }
            }

            is LibrarySettingsEvent.OnCategoryTabVisibleChange -> {
                viewModelScope.launch(Dispatchers.IO) {
                    settingsManager.setShowCategoryTab(event.visible)
                }
            }

            is LibrarySettingsEvent.OnDefaultCategoryTabVisibleChange -> {
                viewModelScope.launch(Dispatchers.IO) {
                    settingsManager.setAlwaysShowDefaultCategoryTab(event.visible)
                }
            }

            is LibrarySettingsEvent.OnCreateCategory -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val categoryName = event.name
                    if (categoryRepository.countCategoriesByName(categoryName) > 0) {
                        logW {
                            "分类名称[${categoryName}]已存在"
                        }
                        _effects.emit(CategoryNameExists)
                        return@launch
                    }

                    val category = CategoryEntity(
                        name = categoryName,
                    )

                    categoryRepository.insertCategory(category)
                }
            }


            is LibrarySettingsEvent.OnEditCategoryClick -> {
                viewModelScope.launch {
                    // 打开编辑分类名称对话框
                    _effects.emit(OpenEditCategoryDialog(event.category))
                }
            }

            is LibrarySettingsEvent.OnDeleteCategoryClick -> {
                viewModelScope.launch {
                    // 打开删除分类对话框
                    _effects.emit(OpenDeleteCategoryDialog(event.categoryId))
                }
            }

            is LibrarySettingsEvent.OnUpdateCategory -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val category = event.category
                    val categoryName = category.name
                    if (categoryRepository.countCategoriesByName(categoryName) > 0) {
                        logW {
                            "分类名称[${categoryName}]已存在"
                        }
                        _effects.emit(CategoryNameExists)
                        return@launch
                    }

                    categoryRepository.updateCategory(category)
                }
            }

            is LibrarySettingsEvent.OnDeleteCategory -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val id = event.categoryId
                    logI {
                        "删除分类，分类ID：${id}"
                    }

                    if (categoryRepository.getCategoryById(id) == null) {
                        logW {
                            "要删除的分类不存在，分类ID：${id}"
                        }
                        return@launch
                    }

                    categoryRepository.deleteCategory(event.categoryId)
                }
            }
        }
    }
}