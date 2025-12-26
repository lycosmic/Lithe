package io.github.lycosmic.lithe.presentation.library

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.lycosmic.lithe.R
import io.github.lycosmic.lithe.data.model.Constants.DOUBLE_CLICK_BACK_INTERVAL_MILLIS
import io.github.lycosmic.lithe.presentation.library.components.BookItem
import io.github.lycosmic.lithe.presentation.library.components.EmptyLibraryState
import io.github.lycosmic.lithe.presentation.library.components.LibraryDeleteBookDialog
import io.github.lycosmic.lithe.presentation.library.components.LibraryTopAppBar
import io.github.lycosmic.lithe.ui.components.ActionItem
import io.github.lycosmic.lithe.ui.components.LitheActionSheet
import io.github.lycosmic.lithe.utils.ToastUtil

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

    val books by viewModel.books.collectAsStateWithLifecycle()

    val selectedBooks by viewModel.selectedBooks.collectAsStateWithLifecycle()

    val topBarState by viewModel.topBarState.collectAsStateWithLifecycle()

    val searchText by viewModel.searchText.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val totalBookCount by viewModel.totalBooksCount.collectAsStateWithLifecycle()

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
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            LibraryTopAppBar(
                libraryTopBarState = topBarState,
                bookCount = totalBookCount,
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

        },
    ) { innerPadding ->

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
    }
}