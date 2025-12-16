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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MoveUp
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
    var showBottomSheet by remember { mutableStateOf(false) }

    val books by viewModel.books.collectAsStateWithLifecycle()

    val selectedBooks by viewModel.selectedBooks.collectAsStateWithLifecycle()

    val isSelectionMode by viewModel.isSelectionMode.collectAsStateWithLifecycle()

    val context = LocalContext.current

    // 双击返回键退出
    val isDoubleBackToExitEnabled by viewModel.isDoubleBackToExitEnabled.collectAsStateWithLifecycle()

    var lastBackPressTime by remember { mutableLongStateOf(0L) }

    // 拦截返回键
    BackHandler(enabled = true) {
        if (isDoubleBackToExitEnabled) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastBackPressTime < DOUBLE_CLICK_BACK_INTERVAL_MILLIS) {
                (context as? Activity)?.finish()
            } else {
                // 第一次按下
                lastBackPressTime = currentTime
                ToastUtil.show(R.string.double_tap_to_exit_message_again)
            }
        } else {
            // 直接退出
            (context as? Activity)?.finish()
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
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            if (isSelectionMode) {
                TopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                viewModel.onEvent(LibraryEvent.OnCancelSelectionClicked)
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "取消选择")
                        }
                    },
                    title = {
                        Text(text = "已选择 ${selectedBooks.size} 本")
                    },
                    actions = {
                        IconButton(onClick = {
                            viewModel.onEvent(LibraryEvent.OnSelectAllClicked)
                        }) {
                            Icon(imageVector = Icons.Default.SelectAll, contentDescription = "全选")
                        }

                        // 移动
                        IconButton(onClick = {
                            viewModel.onEvent(LibraryEvent.OnMoveClicked)
                        }) {
                            Icon(imageVector = Icons.Default.MoveUp, contentDescription = "移动")
                        }

                        IconButton(onClick = {
                            viewModel.onEvent(LibraryEvent.OnDeleteClicked)
                        }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "删除")
                        }

                    }
                )
            } else {
                TopAppBar(
                    title = {
                        Text(
                            text = "书库",
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                showBottomSheet = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "更多"
                            )
                        }
                    },
                )
            }

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

        LitheActionSheet(
            showBottomSheet = showBottomSheet,
            onDismissRequest = {
                showBottomSheet = false
            },
            groups = listOf(
                listOf(
                    ActionItem(
                        text = "关于",
                        icon = null,
                        isDestructive = false,
                        onClick = {
                            showBottomSheet = false
                            viewModel.onEvent(LibraryEvent.OnAboutClicked)
                        }
                    ),
                    ActionItem(
                        text = "帮助",
                        icon = null,
                        isDestructive = false,
                        onClick = {
                            showBottomSheet = false
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
                            showBottomSheet = false
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