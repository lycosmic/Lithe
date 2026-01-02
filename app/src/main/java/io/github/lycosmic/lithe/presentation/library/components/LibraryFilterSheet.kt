package io.github.lycosmic.lithe.presentation.library.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SheetState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.lycosmic.lithe.R
import io.github.lycosmic.lithe.log.logE
import io.github.lycosmic.lithe.presentation.settings.components.GridSizeSlider
import io.github.lycosmic.lithe.presentation.settings.components.SelectionChip
import io.github.lycosmic.lithe.presentation.settings.components.SettingsGroupTitle
import io.github.lycosmic.lithe.presentation.settings.components.SettingsItemWithSwitch
import io.github.lycosmic.lithe.presentation.settings.components.SettingsSubGroupTitle
import io.github.lycosmic.lithe.ui.components.LitheSegmentedButton
import io.github.lycosmic.lithe.util.extensions.labelResId
import io.github.lycosmic.lithe.util.extensions.titleResId
import io.github.lycosmic.model.BookSortType
import io.github.lycosmic.model.BookTitlePosition
import io.github.lycosmic.model.DisplayMode
import io.github.lycosmic.model.OptionItem
import io.github.lycosmic.model.TabType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryFilterSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    // --- 排序 ----
    sortType: BookSortType, // 排序方式
    isAscending: Boolean,
    onSortTypeChanged: (sortType: BookSortType, isAscending: Boolean) -> Unit,
    // --- 显示 ---
    displayMode: DisplayMode,
    onDisplayModeChanged: (displayMode: DisplayMode) -> Unit,
    gridSize: Int,
    onGridSizeChanged: (gridSize: Int) -> Unit,
    bookTitlePosition: BookTitlePosition,
    onBookTitlePositionChanged: (bookTitlePosition: BookTitlePosition) -> Unit,
    isReadButtonVisible: Boolean,
    onReadButtonVisibleChange: (isVisible: Boolean) -> Unit,
    isProgressVisible: Boolean,
    onProgressVisibleChange: (isVisible: Boolean) -> Unit,
    isCategoryTabVisible: Boolean,
    onCategoryTabVisibleChange: (isVisible: Boolean) -> Unit,
    isDefaultCategoryTabVisible: Boolean,
    onDefaultCategoryTabVisibleChange: (isVisible: Boolean) -> Unit,
    isBookCountVisible: Boolean,
    onBookCountVisibleChange: (isVisible: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    if (!visible) return
    ModalBottomSheet(
        dragHandle = null, // 去掉默认的拖动手柄
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {

        var selectedTab by remember { mutableStateOf(value = TabType.SORT) }

        Column(modifier = Modifier.padding(bottom = 12.dp)) {

            PrimaryTabRow(
                // 特殊处理
                selectedTabIndex = if (selectedTab == TabType.SORT) selectedTab.ordinal else selectedTab.ordinal - 1,
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = { HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant) }
            ) {
                TabType.entries.filter {
                    // 去除过滤标签
                    it != TabType.FILTER
                }.forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = {
                            Text(
                                text = stringResource(id = tab.titleResId),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                when (selectedTab) {
                    TabType.SORT -> {
                        SortContent(
                            currentSortType = sortType,
                            isAscending = isAscending,
                            onSortChange = onSortTypeChanged
                        )
                    }

                    TabType.DISPLAY -> {
                        DisplayContent(
                            currentDisplayMode = displayMode,
                            onDisplayModeChanged = onDisplayModeChanged,
                            currentGridSize = gridSize,
                            onGridSizeChanged = onGridSizeChanged,
                            currentBookTitlePosition = bookTitlePosition,
                            onBookTitlePositionChanged = onBookTitlePositionChanged,
                            isReadButtonVisible = isReadButtonVisible,
                            onReadButtonVisibleChange = onReadButtonVisibleChange,
                            isProgressVisible = isProgressVisible,
                            onProgressVisibleChange = onProgressVisibleChange,
                            isCategoryTabVisible = isCategoryTabVisible,
                            onCategoryTabVisibleChange = onCategoryTabVisibleChange,
                            isDefaultCategoryTabVisible = isDefaultCategoryTabVisible,
                            onDefaultCategoryTabVisibleChange = onDefaultCategoryTabVisibleChange,
                            isBookCountVisible = isBookCountVisible,
                            onBookCountVisibleChange = onBookCountVisibleChange,
                        )
                    }

                    TabType.FILTER -> {
                        logE {
                            "书籍过滤器中不显示过滤标签"
                        }
                    }
                }
            }
        }
    }
}

/**
 * 排序标签页
 */
@Composable
private fun SortContent(
    currentSortType: BookSortType,
    isAscending: Boolean, // 排序是否为升序
    onSortChange: (BookSortType, Boolean) -> Unit
) {
    Column {
        BookSortType.entries.forEach { type ->
            val isSelected = currentSortType == type

            val rotation by animateFloatAsState(
                targetValue = if (isSelected && !isAscending) 180f else 0f,
                label = "arrow_rotation"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isSelected) {
                            // 如果点击的是当前选中项，切换升序/降序
                            onSortChange(type, !isAscending)
                        } else {
                            // 如果是其他项，默认降序
                            onSortChange(type, false)
                        }
                    }
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = if (isAscending) stringResource(id = R.string.ascending_order) else stringResource(
                                id = R.string.descending_order
                            ),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.rotate(rotation)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = stringResource(id = type.labelResId),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

/**
 * 显示标签页
 */
@Composable
private fun DisplayContent(
    // 书籍显示模式
    currentDisplayMode: DisplayMode,
    onDisplayModeChanged: (DisplayMode) -> Unit,
    // 网格大小
    currentGridSize: Int,
    onGridSizeChanged: (Int) -> Unit,
    // 标题位置
    currentBookTitlePosition: BookTitlePosition,
    onBookTitlePositionChanged: (BookTitlePosition) -> Unit,
    // 显示阅读按钮
    isReadButtonVisible: Boolean,
    onReadButtonVisibleChange: (Boolean) -> Unit,
    // 显示进度
    isProgressVisible: Boolean,
    onProgressVisibleChange: (Boolean) -> Unit,
    // 显示分类标签页
    isCategoryTabVisible: Boolean,
    onCategoryTabVisibleChange: (Boolean) -> Unit,
    // 始终显示默认标签页
    isDefaultCategoryTabVisible: Boolean,
    onDefaultCategoryTabVisibleChange: (Boolean) -> Unit,
    // 书籍数是否显示
    isBookCountVisible: Boolean,
    onBookCountVisibleChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        // --- 显示模式 ---
        SettingsSubGroupTitle(
            title = stringResource(R.string.display_mode),
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
        )

        // --- 网格或列表 ---
        LitheSegmentedButton(
            modifier = Modifier.padding(horizontal = 16.dp),
            items = DisplayMode.entries.reversed().map { displayMode ->
                OptionItem(
                    value = displayMode,
                    label = stringResource(displayMode.labelResId),
                    selected = displayMode == currentDisplayMode
                )
            },
            onClick = onDisplayModeChanged
        )

        // --- 网格大小和标题位置 ---
        AnimatedVisibility(
            visible = currentDisplayMode == DisplayMode.Grid,
            enter = slideInVertically(
                animationSpec = tween(),
                initialOffsetY = { -it / 2 }
            ) + fadeIn(),
            exit = slideOutVertically(
                animationSpec = tween(),
                targetOffsetY = { -it / 2 }
            ) + fadeOut(),
        ) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))

                GridSizeSlider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    value = currentGridSize,
                    onValueChange = onGridSizeChanged,
                )

                SettingsSubGroupTitle(
                    title = stringResource(R.string.title_position),
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BookTitlePosition.entries.forEach { titlePosition ->
                        SelectionChip(
                            text = stringResource(titlePosition.labelResId),
                            isSelected = currentBookTitlePosition == titlePosition,
                        ) {
                            onBookTitlePositionChanged(titlePosition)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- 显示阅读按钮 ---
        SettingsItemWithSwitch(
            title = stringResource(R.string.show_read_button),
            checked = isReadButtonVisible,
            isDividerVisible = true,
            onCheckedChange = onReadButtonVisibleChange
        )

        // --- 显示进度 ---
        SettingsItemWithSwitch(
            title = stringResource(R.string.show_progress),
            checked = isProgressVisible,
            isDividerVisible = true,
            onCheckedChange = onProgressVisibleChange,
        )

        Spacer(modifier = Modifier.height(16.dp))

        HorizontalDivider()

        // --- 标签页 ---
        SettingsGroupTitle(
            title = {
                stringResource(R.string.tab_title)
            },
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
        )

        SettingsItemWithSwitch(
            title = stringResource(R.string.show_category_tab),
            checked = isCategoryTabVisible,
            isDividerVisible = true,
            onCheckedChange = {
                onCategoryTabVisibleChange(it)
            },
        )

        SettingsItemWithSwitch(
            title = stringResource(R.string.always_show_default_tab),
            checked = isDefaultCategoryTabVisible,
            isDividerVisible = true,
            onCheckedChange = {
                onDefaultCategoryTabVisibleChange(it)
            },
        )

        SettingsItemWithSwitch(
            title = stringResource(R.string.show_book_count),
            checked = isBookCountVisible,
            isDividerVisible = true,
            onCheckedChange = {
                onBookCountVisibleChange(it)
            },
        )
    }
}
