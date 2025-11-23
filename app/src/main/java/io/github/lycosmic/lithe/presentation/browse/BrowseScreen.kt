package io.github.lycosmic.lithe.presentation.browse

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.lycosmic.lithe.data.model.FileItem
import io.github.lycosmic.lithe.ui.components.ActionItem
import io.github.lycosmic.lithe.ui.components.LitheActionSheet
import io.github.lycosmic.lithe.ui.components.RoundCheckbox
import io.github.lycosmic.lithe.utils.FileUtils

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

    // 以文件夹分组的文件列表
    val directoryWithFiles by browseViewModel.groupedFiles.collectAsStateWithLifecycle()

    // 当前选中的文件
    val selectedFiles by browseViewModel.selectedFiles.collectAsStateWithLifecycle()

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

                        // 确认导入
                        IconButton(
                            onClick = {
                                browseViewModel.onEvent(BrowseEvent.OnImportBooksClick)
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
    }
}

// --- 空文件夹 ---
@Composable
private fun EmptyBrowseScreen(
    modifier: Modifier = Modifier,
    onNavigateToBrowseSettings: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "通过下载扩展图书馆的藏书",
            style = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        TextButton(
            onClick = onNavigateToBrowseSettings
        ) {
            Text(
                text = "设置扫描",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.secondary
                )
            )
        }
    }
}

// --- 文件夹标题 ---
@Composable
fun DirectoryHeader(path: String, modifier: Modifier = Modifier, onPinClick: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = path,
                style = MaterialTheme.typography.bodyLarge.copy(
                    // 尽量填满一行
                    lineBreak = LineBreak.Simple
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            IconButton(onClick = onPinClick) {
                Icon(
                    imageVector = Icons.Outlined.PushPin,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// --- 文件行 ---
@Composable
fun FileRowItem(
    file: FileItem,
    isSelected: Boolean, // 是否被选中
    isMultiSelectMode: Boolean, // 是否是多选模式
    onCheckboxClick: (Boolean) -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    // 选中的背景色
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    } else {
        Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color = backgroundColor)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Surface(
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline,
                    RoundedCornerShape(8.dp)
                )
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = file.name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    // 尽量少换行, 尽量填满一行
                    lineBreak = LineBreak.Simple
                ),
                overflow = TextOverflow.Clip,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${FileUtils.formatFileSize(file.size)}, ${FileUtils.formatDate(file.lastModified)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // 多选框区域
        Row(
            modifier = Modifier
                .padding(end = 12.dp)
                .size(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // 显示多选框Checkbox
            AnimatedVisibility(
                visible = isMultiSelectMode || isSelected,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                RoundCheckbox(
                    checked = isSelected,
                    onCheckedChange = onCheckboxClick
                )
            }
        }

    }
}