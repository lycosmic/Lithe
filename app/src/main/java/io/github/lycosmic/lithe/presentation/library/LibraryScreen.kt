package io.github.lycosmic.lithe.presentation.library

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.lycosmic.lithe.R
import io.github.lycosmic.lithe.data.model.Constants.DOUBLE_CLICK_BACK_INTERVAL_MILLIS
import io.github.lycosmic.lithe.presentation.library.components.BookItem
import io.github.lycosmic.lithe.presentation.library.components.CategoryTabRow
import io.github.lycosmic.lithe.presentation.library.components.EmptyLibraryState
import io.github.lycosmic.lithe.presentation.library.components.LibraryDeleteBookDialog
import io.github.lycosmic.lithe.presentation.library.components.LibraryFilterSheet
import io.github.lycosmic.lithe.presentation.library.components.LibraryTopAppBar
import io.github.lycosmic.lithe.ui.components.ActionItem
import io.github.lycosmic.lithe.ui.components.LitheActionSheet
import io.github.lycosmic.lithe.utils.ToastUtil
import io.github.lycosmic.lithe.utils.toast
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = hiltViewModel(),
    onGoToSettings: () -> Unit,
    onGoToAbout: () -> Unit,
    onGoToHelp: () -> Unit,
    onGoToBookDetail: (Long) -> Unit,
    onGoToBookRead: (Long) -> Unit,
    onGoToBrowser: () -> Unit,
) {
    var showMoreOptionsBottomSheet by remember { mutableStateOf(false) }

    var showFilterBottomSheet by remember { mutableStateOf(false) }

    val categoryWithBooksList by viewModel.categoryWithBooksList.collectAsStateWithLifecycle()

    val selectedBooks by viewModel.selectedBooks.collectAsStateWithLifecycle()

    val topBarState by viewModel.topBarState.collectAsStateWithLifecycle()

    val searchText by viewModel.searchText.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val totalBookCount by viewModel.totalBooksCount.collectAsStateWithLifecycle()

    // 排序方式和顺序
    val sortType by viewModel.sortType.collectAsStateWithLifecycle()
    val sortOrder by viewModel.sortOrder.collectAsStateWithLifecycle()

    // 显示模式
    val bookDisplayMode by viewModel.bookDisplayMode.collectAsStateWithLifecycle()

    // 网格大小和标题位置
    val bookGridColumnCount by viewModel.bookGridColumnCount.collectAsStateWithLifecycle()
    val bookTitlePosition by viewModel.bookTitlePosition.collectAsStateWithLifecycle()

    // 显示阅读按钮和阅读进度
    val showReadButton by viewModel.showReadButton.collectAsStateWithLifecycle()
    val showReadProgress by viewModel.showReadProgress.collectAsStateWithLifecycle()

    // 显示分类标签和显示默认标签页
    val showCategoryTab by viewModel.showCategoryTab.collectAsStateWithLifecycle()
    val alwaysShowDefaultCategoryTab by viewModel.alwaysShowDefaultCategoryTab.collectAsStateWithLifecycle()

    // 显示书籍数量
    val isBookCountVisible by viewModel.showBookCount.collectAsStateWithLifecycle()

    // 删除书籍确认对话框是否可见
    var isDeleteBookDialogVisible by remember { mutableStateOf(false) }

    // 处于搜索模式下
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()

    // 处于多选模式下
    val isSelectionMode by viewModel.isSelectionMode.collectAsStateWithLifecycle()

    // 双击返回键退出
    val isDoubleBackToExitEnabled by viewModel.isDoubleBackToExitEnabled.collectAsStateWithLifecycle()

    // 上一次按下返回的时间
    var lastBackPressTime by remember { mutableLongStateOf(0L) }

    // 分页状态
    val pagerState = rememberPagerState(pageCount = { categoryWithBooksList.size })

    // 当前选中的标签名称
    val currentCategoryName by remember(pagerState.currentPage) {
        mutableStateOf(
            categoryWithBooksList.getOrNull(pagerState.currentPage)?.category?.name ?: ""
        )
    }


    val scope = rememberCoroutineScope()

    // 拦截返回键
    BackHandler(enabled = isDoubleBackToExitEnabled || isSearching || isSelectionMode) {
        // 优先级：多选模式 > 搜索模式 > 默认模式
        when {
            isSelectionMode -> {
                viewModel.onEvent(LibraryEvent.OnCancelSelectionClicked)
            }

            isSearching -> {
                viewModel.onEvent(LibraryEvent.OnExitSearchClicked)
            }

            isDoubleBackToExitEnabled -> {
                // 处于双击返回键退出模式下
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastBackPressTime < DOUBLE_CLICK_BACK_INTERVAL_MILLIS) {
                    (context as? Activity)?.finish()
                } else {
                    // 第一次按下
                    lastBackPressTime = currentTime
                    ToastUtil.show(R.string.double_tap_to_exit_message_again)
                }
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is LibraryEffect.OnNavigateToSettings -> {
                    onGoToSettings()
                }

                is LibraryEffect.OnNavigateToAbout -> {
                    onGoToAbout()
                }

                is LibraryEffect.OnNavigateToHelp -> {
                    onGoToHelp()
                }

                is LibraryEffect.OnNavigateToBookDetail -> {
                    onGoToBookDetail(effect.bookId)
                }

                is LibraryEffect.OnNavigateToBookRead -> {
                    onGoToBookRead(effect.bookId)
                }

                LibraryEffect.OnNavigateToBrowser -> {
                    onGoToBrowser()
                }

                LibraryEffect.OpenFilterBottomSheet -> {
                    showFilterBottomSheet = true
                }

                LibraryEffect.OpenMoreOptionsBottomSheet -> {
                    showMoreOptionsBottomSheet = true
                }

                LibraryEffect.CloseDeleteBookConfirmDialog -> {
                    isDeleteBookDialogVisible = false
                }

                LibraryEffect.CloseFilterBottomSheet -> {
                    showFilterBottomSheet = false
                }

                LibraryEffect.CloseMoreOptionsBottomSheet -> {
                    showMoreOptionsBottomSheet = false
                }

                LibraryEffect.ShowDeleteBookConfirmDialog -> {
                    isDeleteBookDialogVisible = true
                }

                LibraryEffect.ShowDeleteBookSuccessToast -> {
                    R.string.delete_all_selected_book_success.toast()
                }

                is LibraryEffect.ShowDeleteBookCountToast -> {
                    R.string.delete_count_book_success.toast()
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Column {
                LibraryTopAppBar(
                    libraryTopBarState = topBarState,
                    bookCount = totalBookCount,
                    currentCategoryName = currentCategoryName,
                    showCategoryTab = showCategoryTab,
                    isBookCountVisible = isBookCountVisible,
                    selectedBookCount = selectedBooks.size,
                    searchText = searchText,
                    onSearchTextChange = {
                        viewModel.onEvent(LibraryEvent.OnSearchTextChanged(it))
                    },
                    onExitSearchClick = {
                        viewModel.onEvent(LibraryEvent.OnExitSearchClicked)
                    },
                    onSearchClick = {
                        viewModel.onEvent(LibraryEvent.OnSearchClicked)
                    },
                    onFilterClick = {
                        viewModel.onEvent(LibraryEvent.OnFilterClicked)
                    },
                    onMoreClick = {
                        viewModel.onEvent(LibraryEvent.OnMoreClicked)
                    },
                    onCancelSelectClick = {
                        viewModel.onEvent(LibraryEvent.OnCancelSelectionClicked)
                    },
                    onSelectAllClick = {
                        viewModel.onEvent(LibraryEvent.OnSelectAllClicked)
                    },
                    onMoveCategoryClick = {
                        viewModel.onEvent(LibraryEvent.OnMoveCategoryClicked)
                    },
                    onDeleteClick = {
                        viewModel.onEvent(LibraryEvent.OnDeleteClicked)
                    },
                )

                AnimatedVisibility(
                    visible = showCategoryTab && categoryWithBooksList.isNotEmpty()
                ) {
                    CategoryTabRow(
                        categories = categoryWithBooksList.map {
                            it.category
                        },
                        bookCountsList = categoryWithBooksList.map { bookGroup ->
                            bookGroup.books.size
                        },
                        isBookCountVisible = isBookCountVisible,
                        selectedIndex = pagerState.currentPage,
                        onTabSelected = { index ->
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                    )
                }
            }

        },
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { pagerIndex ->
            val books = categoryWithBooksList[pagerIndex].books
            if (books.isEmpty()) {
                // 引导页
                EmptyLibraryState(modifier = Modifier.padding(paddingValues = innerPadding)) {
                    viewModel.onEvent(LibraryEvent.OnAddBookClicked)
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3), // TODO 手动调整列数
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                ) {
                    items(items = books, key = { book ->
                        book.id
                    }) { book ->
                        BookItem(
                            title = book.title,
                            coverPath = book.coverPath,
                            readProgress = book.progress,
                            isSelected = selectedBooks.contains(book.id),
                            onClick = {
                                viewModel.onEvent(LibraryEvent.OnBookClicked(book.id))
                            },
                            onLongClick = {
                                viewModel.onEvent(LibraryEvent.OnBookLongClicked(book.id))
                            }
                        )
                    }
                }
            }
        }



        if (isDeleteBookDialogVisible) {
            LibraryDeleteBookDialog(
                deleteBookCount = selectedBooks.size,
                onDismissRequest = {
                    viewModel.onEvent(LibraryEvent.OnDeleteDialogDismissed)
                },
                onConfirm = {
                    viewModel.onEvent(LibraryEvent.OnDeleteDialogConfirmed)
                }
            )
        }


        LitheActionSheet(
            showBottomSheet = showMoreOptionsBottomSheet,
            onDismissRequest = {
                showMoreOptionsBottomSheet = false
            },
            groups = listOf(
                listOf(
                    ActionItem(
                        text = "关于",
                        icon = null,
                        isDestructive = false,
                        onClick = {
                            showMoreOptionsBottomSheet = false
                            viewModel.onEvent(LibraryEvent.OnAboutClicked)
                        }
                    ),
                    ActionItem(
                        text = "帮助",
                        icon = null,
                        isDestructive = false,
                        onClick = {
                            showMoreOptionsBottomSheet = false
                            viewModel.onEvent(LibraryEvent.OnHelpClicked)
                        }
                    )
                ),
                listOf(
                    ActionItem(
                        text = "设置",
                        icon = null,
                        isDestructive = false,
                        onClick = {
                            showMoreOptionsBottomSheet = false
                            viewModel.onEvent(LibraryEvent.OnSettingsClicked)
                        },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
            )
        )

        LibraryFilterSheet(
            visible = showFilterBottomSheet,
            onDismissRequest = {
                viewModel.onEvent(LibraryEvent.OnFilterBottomSheetDismissed)
            },
            sortType = sortType,
            isAscending = sortOrder,
            onSortTypeChanged = { sortOrder, isAscending ->
                viewModel.onEvent(LibraryEvent.OnSortChanged(sortOrder, isAscending))
            },
            displayMode = bookDisplayMode,
            onDisplayModeChanged = {
                viewModel.onEvent(LibraryEvent.OnDisplayModeChanged(it))
            },
            gridSize = bookGridColumnCount,
            onGridSizeChanged = {
                viewModel.onEvent(LibraryEvent.OnGridSizeChanged(it))
            },
            bookTitlePosition = bookTitlePosition,
            onBookTitlePositionChanged = {
                viewModel.onEvent(LibraryEvent.OnBookTitlePositionChanged(it))
            },
            isReadButtonVisible = showReadButton,
            onReadButtonVisibleChange = {
                viewModel.onEvent(LibraryEvent.OnReadButtonVisibleChanged(it))
            },
            isProgressVisible = showReadProgress,
            onProgressVisibleChange = {
                viewModel.onEvent(LibraryEvent.OnProgressVisibleChanged(it))
            },
            isCategoryTabVisible = showCategoryTab,
            onCategoryTabVisibleChange = {
                viewModel.onEvent(LibraryEvent.OnCategoryTabVisibleChanged(it))
            },
            isDefaultCategoryTabVisible = alwaysShowDefaultCategoryTab,
            onDefaultCategoryTabVisibleChange = {
                viewModel.onEvent(LibraryEvent.OnDefaultCategoryTabVisibleChanged(it))
            },
            isBookCountVisible = isBookCountVisible,
            onBookCountVisibleChange = {
                viewModel.onEvent(LibraryEvent.OnBookCountVisibleChanged(it))
            }
        )
    }
}