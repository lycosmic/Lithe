package io.github.lycosmic.lithe.presentation.library.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MoveUp
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.lycosmic.lithe.R
import io.github.lycosmic.lithe.presentation.library.LibraryTopBarState


@Composable
fun LibraryTopAppBar(
    libraryTopBarState: LibraryTopBarState,
    bookCount: Int,
    isBookCountVisible: Boolean,
    selectedBookCount: Int,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    onMoreClick: () -> Unit,
    onCancelSelectClick: () -> Unit,
    onSelectAllClick: () -> Unit,
    onExitSearchClick: () -> Unit,
    onMoveCategoryClick: () -> Unit,
    onDeleteClick: () -> Unit,
    showCategoryTab: Boolean,
    currentCategoryName: String,
) {
    when (libraryTopBarState) {
        LibraryTopBarState.DEFAULT -> DefaultLibraryTopAppBar(
            bookCount = bookCount,
            isBookCountVisible = isBookCountVisible,
            categoryName = currentCategoryName,
            isCategoryTabVisible = showCategoryTab,
            onSearchClick = onSearchClick,
            onFilterClick = onFilterClick,
            onMoreClick = onMoreClick
        )

        LibraryTopBarState.SEARCH_BOOK -> SearchLibraryTopAppBar(
            searchText,
            onSearchTextChange,
            onExitSearchClick = onExitSearchClick,
            onMoreClick = onMoreClick
        )

        LibraryTopBarState.SELECT_BOOK -> SelectLibraryTopAppBar(
            selectedBookCount,
            onCancelSelectClick,
            onSelectAllClick,
            onMoveCategoryClick,
            onDeleteClick
        )
    }
}


/**
 * 默认状态下的书库顶部栏
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultLibraryTopAppBar(
    bookCount: Int,
    isBookCountVisible: Boolean,
    onSearchClick: () -> Unit,
    onFilterClick: () -> Unit,
    onMoreClick: () -> Unit,
    isCategoryTabVisible: Boolean,
    categoryName: String,
) {

    val titleText = when {
        isCategoryTabVisible -> stringResource(R.string.library)
        else -> {
            categoryName.ifEmpty {
                stringResource(R.string.default_category_name)
            }
        }
    }

    TopAppBar(
        title = {
            Row {
                Text(
                    text = titleText,
                )

                Spacer(modifier = Modifier.width(6.dp))

                // 数字角标
                CountBadge(
                    count = bookCount,
                    visible = isBookCountVisible,
                )
            }
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
 * 搜索状态下的书库顶部栏
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchLibraryTopAppBar(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onExitSearchClick: () -> Unit, // 退出搜索
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
        }
    )
}

/**
 * 选择状态下的书库顶部栏
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectLibraryTopAppBar(
    selectedBookCount: Int, // 选中的书籍数
    onCancelSelectClick: () -> Unit, // 取消所有选择
    onSelectAllClick: () -> Unit, // 全选所有书籍
    onMoveCategoryClick: () -> Unit, // 移动所选书籍的分类
    onDeleteClick: () -> Unit, // 删除所选书籍
) {
    TopAppBar(
        navigationIcon = {
            IconButton(
                onClick = onCancelSelectClick
            ) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = stringResource(R.string.cancel_selection)
                )
            }
        },
        title = {
            Text(text = stringResource(R.string.selected_books, selectedBookCount))
        },
        actions = {
            IconButton(
                onClick =
                    onSelectAllClick
            ) {
                Icon(
                    imageVector = Icons.Default.SelectAll,
                    contentDescription = stringResource(R.string.select_all_books)
                )
            }

            // 移动分类
            IconButton(
                onClick =
                    onMoveCategoryClick
            ) {
                Icon(
                    imageVector = Icons.Default.MoveUp,
                    contentDescription = stringResource(R.string.move_selected_books)
                )
            }

            IconButton(
                onClick =
                    onDeleteClick
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete_selected_books)
                )
            }

        }
    )
}
