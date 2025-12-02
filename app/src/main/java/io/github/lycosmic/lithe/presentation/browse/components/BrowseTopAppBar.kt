package io.github.lycosmic.lithe.presentation.browse.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import io.github.lycosmic.lithe.R

/**
 * 默认状态下的浏览页顶部栏
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultBrowseTopAppBar(
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    onMoreClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.browse),
            )
        },
        actions = {
            IconButton(
                onClick = onSearchClick
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = stringResource(R.string.search)
                )
            }

            IconButton(
                onClick = onFilterClick
            ) {
                Icon(
                    Icons.Default.FilterList,
                    contentDescription = stringResource(R.string.select)
                )
            }

            IconButton(
                onClick = onMoreClick
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.more),
                )
            }
        },
    )
}

/**
 * 搜索状态下的浏览页顶部栏
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBrowseTopAppBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onExitSearchClick: () -> Unit,
    onMoreClick: () -> Unit,
) {
    TopAppBar(
        title = {
            TextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        text = stringResource(R.string.search_hint),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                )
            )
        },
        navigationIcon = {
            // 退出搜索
            IconButton(onClick = {
                onExitSearchClick()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.exit_search)
                )
            }
        },
        actions = {
            // 更多
            IconButton(
                onClick = {
                    onMoreClick()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.more),
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    )
}

/**
 * 选择状态下的浏览页顶部栏
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectBrowseTopAppBar(
    selectedFileSize: Int,
    onCancelSelectClick: () -> Unit,
    onToggleAllFileClick: () -> Unit,
    onAddFileClick: () -> Unit,
) {
    TopAppBar(
        title = { Text(stringResource(R.string.selected_files, selectedFileSize)) },
        navigationIcon = {
            // 清除选中项目
            IconButton(onClick = {
                onCancelSelectClick()
            }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.cancel_selection)
                )
            }
        },
        actions = {
            // 选择所有文件
            IconButton(
                onClick = {
                    onToggleAllFileClick()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.SelectAll,
                    contentDescription = stringResource(R.string.toggle_all_selection)
                )
            }

            // 添加文件
            IconButton(
                onClick = {
                    onAddFileClick()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(R.string.add)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    )
}
