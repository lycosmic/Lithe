package io.github.lycosmic.lithe.presentation.browse.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import io.github.lycosmic.lithe.domain.model.DEFAULT_IS_ASCENDING
import io.github.lycosmic.lithe.domain.model.SortType
import io.github.lycosmic.lithe.domain.model.TabType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseFilterSheet(
    onDismissRequest: () -> Unit,
    currentSortType: SortType,
    isAscending: Boolean,
    onSortTypeChanged: (sortType: SortType, isAscending: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState()
) {

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
                selectedTabIndex = selectedTab.ordinal,
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                contentColor = MaterialTheme.colorScheme.primary,
                divider = { HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant) }
            ) {
                TabType.entries.forEach { tab ->
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
                            currentSortType = currentSortType,
                            isAscending = isAscending,
                            onSortChange = onSortTypeChanged
                        )
                    }

                    TabType.FILTER -> {
                        Text("过滤选项开发中...")
                    }

                    TabType.DISPLAY -> {
                        Text("过滤选项开发中...")
                    }
                }
            }
        }
    }
}

/**
 * 排序列表内容
 */
@Composable
private fun SortContent(
    currentSortType: SortType,
    isAscending: Boolean,
    onSortChange: (SortType, Boolean) -> Unit
) {
    Column {
        SortType.entries.forEach { type ->
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
                            onSortChange(type, DEFAULT_IS_ASCENDING)
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
                    text = stringResource(id = type.displayNameResId),
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}