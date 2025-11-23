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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.lycosmic.lithe.presentation.browse.components.AddBookConfirmationDialog
import io.github.lycosmic.lithe.presentation.browse.components.DirectoryHeader
import io.github.lycosmic.lithe.presentation.browse.components.EmptyBrowseScreen
import io.github.lycosmic.lithe.presentation.browse.components.FileRowItem
import io.github.lycosmic.lithe.ui.components.ActionItem
import io.github.lycosmic.lithe.ui.components.LitheActionSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseScreen(
    modifier: Modifier = Modifier,
    browseViewModel: BrowseViewModel = hiltViewModel(),
    onNavigateToBrowseSettings: () -> Unit,
    onGoToSettings: () -> Unit,
    onGoToAbout: () -> Unit,
    onGoToHelp: () -> Unit
) {
    // 控制底部抽屉是否显示
    var showBottomSheet by remember { mutableStateOf(false) }

    // 控制添加书籍对话框是否显示
    var showAddBookDialog by remember { mutableStateOf(false) }

    // 以文件夹分组的文件列表
    val directoryWithFiles by browseViewModel.groupedFiles.collectAsStateWithLifecycle()

    // 当前选中的文件
    val selectedFiles by browseViewModel.selectedFiles.collectAsStateWithLifecycle()

    // 当前待导入的书籍
    val bookToImport by browseViewModel.bookToImport.collectAsStateWithLifecycle()

    val isMultiSelectMode by
    browseViewModel.isMultiSelectMode.collectAsStateWithLifecycle(initialValue = false)

    val isLoading by browseViewModel.isLoading.collectAsStateWithLifecycle()

    // 拦截系统返回键
    BackHandler(enabled = isMultiSelectMode) {
        // 退出选中状态
        browseViewModel.onEvent(BrowseEvent.OnBackClick)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            // 动态切换 TopBar
            if (isMultiSelectMode) {
                TopAppBar(
                    title = { Text("已选择 ${selectedFiles.size} 项") },
                    navigationIcon = {
                        IconButton(onClick = {
                            browseViewModel.onEvent(BrowseEvent.OnCancelClick)
                        }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "取消选择")
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                browseViewModel.onEvent(BrowseEvent.OnToggleAllSelectionClick)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.SelectAll,
                                contentDescription = "全选"
                            )
                        }

                        // 添加书籍
                        IconButton(
                            onClick = {
                                showAddBookDialog = true
                                // 预导入
                                browseViewModel.onEvent(BrowseEvent.OnAddBooksClick)
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "添加")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                )
            } else {
                TopAppBar(
                    title = {
                        Text(
                            text = "浏览",
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = {

                            }
                        ) { Icon(Icons.Default.Search, "搜索") }

                        IconButton(
                            onClick = {

                            }
                        ) { Icon(Icons.Default.FilterList, "筛选") }

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
                        DirectoryHeader(path = path) {
                            // 固定到顶部

                        }
                    }

                    items(files) { file ->
                        FileRowItem(
                            file = file,
                            isSelected = selectedFiles.contains(file.uri.toString()),
                            isMultiSelectMode = isMultiSelectMode,
                            onCheckboxClick = {
                                // 点击复选框
                                browseViewModel.onEvent(BrowseEvent.OnFileCheckboxClick(file))
                            },
                            onClick = {
                                // 点击文件选中该文件
                                browseViewModel.onEvent(BrowseEvent.OnFileClick(file))
                            },
                            onLongClick = {
                                // 长按文件选中,一次性选中该文件夹下的所有文件
                                browseViewModel.onEvent(BrowseEvent.OnFileLongClick(files))
                            }
                        )
                    }

                    // 组之间的间距
                    item { Spacer(modifier = Modifier.height(12.dp)) }
                }
            }
        }

        // 底部抽屉
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
                            onGoToAbout()
                        }
                    ),
                    ActionItem(
                        text = "帮助",
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
                        text = "设置",
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


        // 添加书籍弹窗
        if (showAddBookDialog) {
            AddBookConfirmationDialog(
                selectedBooks = bookToImport,
                onDismissRequest = {
                    browseViewModel.onEvent(BrowseEvent.OnDismissAddBooksDialog)
                    showAddBookDialog = false
                },
                onConfirm = {
                    // 确认添加书籍
                    browseViewModel.onEvent(BrowseEvent.OnImportBooksClick)
                    showAddBookDialog = false
                },
                onCancel = {
                    browseViewModel.onEvent(BrowseEvent.OnDismissAddBooksDialog)
                    showAddBookDialog = false
                },
                onToggleSelection = { newBookToAdd ->
                    browseViewModel.onEvent(BrowseEvent.OnAddBooksDialogItemClick(newBookToAdd))
                }
            )
        }

    }
}