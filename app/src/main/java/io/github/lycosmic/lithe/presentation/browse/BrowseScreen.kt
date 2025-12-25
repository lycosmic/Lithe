package io.github.lycosmic.lithe.presentation.browse

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.lycosmic.lithe.R
import io.github.lycosmic.lithe.presentation.browse.components.AddBookConfirmationDialog
import io.github.lycosmic.lithe.presentation.browse.components.BrowseFilterSheet
import io.github.lycosmic.lithe.presentation.browse.components.DefaultBrowseTopAppBar
import io.github.lycosmic.lithe.presentation.browse.components.DirectoryHeader
import io.github.lycosmic.lithe.presentation.browse.components.EmptyBrowseScreen
import io.github.lycosmic.lithe.presentation.browse.components.FileRowItem
import io.github.lycosmic.lithe.presentation.browse.components.SearchBrowseTopAppBar
import io.github.lycosmic.lithe.presentation.browse.components.SelectBrowseTopAppBar
import io.github.lycosmic.lithe.presentation.browse.model.BrowseTopBarState
import io.github.lycosmic.lithe.ui.components.ActionItem
import io.github.lycosmic.lithe.ui.components.LitheActionSheet
import io.github.lycosmic.lithe.utils.toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(
    modifier: Modifier = Modifier,
    viewModel: BrowseViewModel = hiltViewModel(),
    onNavigateToBrowseSettings: () -> Unit,
    onGoToSettings: () -> Unit,
    onGoToAbout: () -> Unit,
    onGoToHelp: () -> Unit
) {
    // 控制底部抽屉是否显示
    var showBottomSheet by remember { mutableStateOf(false) }

    // 显示过滤器
    var showFilterSheet by remember { mutableStateOf(false) }

    // 控制添加书籍对话框是否显示
    var showAddBookDialog by remember { mutableStateOf(false) }

    // 过滤、排序、文件夹分组的文件列表
    val directoryWithFiles by viewModel.processedFiles.collectAsStateWithLifecycle()

    // 当前选中的文件
    val selectedFiles by viewModel.selectedFiles.collectAsStateWithLifecycle()

    // 当前待导入的书籍
    val bookToImport by viewModel.parsedBooks.collectAsStateWithLifecycle()

    // 是否为多选模式
    val isMultiSelectMode by
    viewModel.isMultiSelectMode.collectAsStateWithLifecycle(initialValue = false)

    val topBarState by viewModel.topBarState.collectAsStateWithLifecycle()

    // 排序类型
    val sortType by viewModel.sortType.collectAsStateWithLifecycle()
    val isAscending by viewModel.isAscending.collectAsStateWithLifecycle()

    // 当前的过滤
    val filter by viewModel.filterList.collectAsStateWithLifecycle()

    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val isDialogLoading by viewModel.isAddBooksDialogLoading.collectAsStateWithLifecycle()

    // 当前搜索的文本
    val searchText by viewModel.searchText.collectAsStateWithLifecycle()

    val isSearchMode by viewModel.isSearchMode.collectAsStateWithLifecycle()

    // 显示模式
    val displayMode by viewModel.fileBrowseDisplayMode.collectAsStateWithLifecycle()

    // 网格大小
    val gridColumnCount by viewModel.gridColumnCount.collectAsStateWithLifecycle()

    // 固定的文件夹头部列表
    val pinnedHeaders by viewModel.pinnedHeaders.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is BrowseEffect.ShowSuccessCountToast -> {
                    R.string.add_selected_books_success.toast(formatArgs = arrayOf(effect.count))
                }

                BrowseEffect.ShowSuccessToast -> {
                    R.string.add_all_selected_books_success.toast()
                }
            }
        }
    }

    // 拦截系统返回键
    BackHandler(enabled = isMultiSelectMode || isSearchMode) {
        // 退出选中状态
        viewModel.onEvent(BrowseEvent.OnBackClick)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            // 动态切换 TopBar
            when (topBarState) {
                BrowseTopBarState.DEFAULT -> {
                    DefaultBrowseTopAppBar(
                        onSearchClick = {
                            viewModel.onEvent(BrowseEvent.OnSearchClick)
                        },
                        onFilterClick = {
                            showFilterSheet = true
                        },
                        onMoreClick = {
                            showBottomSheet = true
                        }
                    )
                }

                BrowseTopBarState.SELECT_FILE -> {
                    SelectBrowseTopAppBar(
                        selectedFileSize = selectedFiles.size,
                        onCancelSelectClick = {
                            viewModel.onEvent(BrowseEvent.OnClearSelectionClick)
                        },
                        onToggleAllFileClick = {
                            viewModel.onEvent(BrowseEvent.OnToggleAllSelectionClick)
                        },
                        onAddFileClick = {
                            showAddBookDialog = true
                            // 预导入
                            viewModel.onEvent(BrowseEvent.OnAddFileClick)
                        }
                    )
                }

                BrowseTopBarState.SEARCH -> {
                    SearchBrowseTopAppBar(
                        searchText = searchText,
                        onSearchTextChange = {
                            viewModel.onEvent(BrowseEvent.OnSearchTextChange(it))
                        },
                        onExitSearchClick = {
                            viewModel.onEvent(BrowseEvent.OnExitSearchClick)
                        },
                        onMoreClick = {
                            showBottomSheet = true
                        }
                    )
                }
            }
        },
    ) { paddings ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (directoryWithFiles.isEmpty()) {
            // 引导添加文件夹
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                EmptyBrowseScreen(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onNavigateToBrowseSettings = onNavigateToBrowseSettings
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddings)
            ) {
                directoryWithFiles.forEach { (path, files) ->
                    // stickyHeader 让路径吸顶
                    stickyHeader {
                        DirectoryHeader(
                            path = path,
                            isPinned = pinnedHeaders.contains(path)
                        ) {
                            // 点击固定按钮，切换固定状态
                            viewModel.onEvent(BrowseEvent.OnPinHeaderClick(path))
                        }
                    }

                    items(files) { file ->
                        FileRowItem(
                            file = file,
                            isSelected = selectedFiles.contains(file.uri.toString()),
                            isMultiSelectMode = isMultiSelectMode,
                            onCheckboxClick = {
                                // 点击复选框
                                viewModel.onEvent(BrowseEvent.OnFileCheckboxClick(file))
                            },
                            onClick = {
                                // 点击文件选中该文件
                                viewModel.onEvent(BrowseEvent.OnFileClick(file))
                            },
                            onLongClick = {
                                // 长按文件选中,一次性选中该文件夹下的所有文件
                                viewModel.onEvent(BrowseEvent.OnFileLongClick(files))
                            }
                        )
                    }

                    // 组之间的间距
                    item { Spacer(modifier = Modifier.height(12.dp)) }
                }
            }
        }

        // 过滤抽屉
        BrowseFilterSheet(
            show = showFilterSheet,
            onDismissRequest = {
                showFilterSheet = false
            },
            currentSortType = sortType,
            isAscending = isAscending,
            onSortTypeChanged = { newSortType, newIsAscending ->
                viewModel.onEvent(
                    BrowseEvent.OnSortTypeChange(
                        newSortType,
                        newIsAscending
                    )
                )
                showFilterSheet = true
            },
            currentFilterOptions = filter,
            onFilterChange = { newFilter ->
                viewModel.onEvent(
                    BrowseEvent.OnFilterChange(
                        newFilter
                    )
                )
                showFilterSheet = true
            },
            currentBrowseDisplayMode = displayMode,
            onDisplayModeChanged = {
                viewModel.onEvent(
                    BrowseEvent.OnDisplayModeChange(
                        it
                    )
                )
            },
            currentGridSize = gridColumnCount,
            onGridSizeChanged = {
                viewModel.onEvent(BrowseEvent.OnGridSizeChange(it))
            }
        )


        // 底部抽屉
        LitheActionSheet(
            showBottomSheet = showBottomSheet,
            onDismissRequest = {
                showBottomSheet = false
            },
            groups = listOf(
                listOf(
                    ActionItem(
                        text = stringResource(R.string.about),
                        icon = null,
                        isDestructive = false,
                        onClick = {
                            showBottomSheet = false
                            onGoToAbout()
                        }
                    ),
                    ActionItem(
                        text = stringResource(R.string.help),
                        icon = null,
                        isDestructive = false,
                        onClick = {
                            showBottomSheet = false
                            onGoToHelp()
                        }
                    )
                ),
                listOf(
                    ActionItem(
                        text = stringResource(R.string.settings),
                        icon = null,
                        isDestructive = false,
                        onClick = {
                            showBottomSheet = false
                            onGoToSettings()
                        },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
            )
        )


        // 添加书籍对话框
        AddBookConfirmationDialog(
            showDialog = showAddBookDialog,
            isDialogLoading = isDialogLoading,
            selectedBooks = bookToImport,
            onDismissRequest = {
                viewModel.onEvent(BrowseEvent.OnDismissAddBooksDialog)
                showAddBookDialog = false
            },
            onConfirm = {
                // 确认添加书籍
                viewModel.onEvent(BrowseEvent.OnImportBooksClick)
                showAddBookDialog = false
            },
            onCancel = {
                viewModel.onEvent(BrowseEvent.OnDismissAddBooksDialog)
                showAddBookDialog = false
            },
            onToggleSelection = { newBookToAdd ->
                viewModel.onEvent(BrowseEvent.OnBookItemClick(newBookToAdd))
            }
        )
    }
}