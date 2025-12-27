package io.github.lycosmic.lithe.presentation.library.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.lycosmic.lithe.data.local.entity.CategoryEntity

/**
 * 书库顶部的分类标签栏
 */
@Composable
fun CategoryTabRow(
    categories: List<CategoryEntity>,
    bookCounts: List<Int>,
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit, // 点击分类标签
    modifier: Modifier = Modifier,
) {
    SecondaryScrollableTabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier,
        edgePadding = 16.dp,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        divider = {
            // 底部分割线
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        },
        indicator = {
            // 底部指示条
            TabRowDefaults.SecondaryIndicator(
                Modifier
                    .tabIndicatorOffset(
                        selectedTabIndex = selectedIndex,
                        matchContentSize = false
                    )
                    .padding(horizontal = 16.dp),
                height = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    ) {
        categories.forEachIndexed { index, category ->
            // 是否被选中
            val isSelected = selectedIndex == index

            Tab(
                selected = isSelected,
                onClick = {
                    onTabSelected(index)
                },
                text = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // 分类名称
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        // 分类书籍数
                        CountBadge(
                            count = bookCounts[index]
                        )
                    }
                }
            )

        }
    }
}

@Preview
@Composable
private fun CategoryTabRowPrev() {
    val categories = remember {
        listOf(
            CategoryEntity(1, "默认"),
            CategoryEntity(2, "轻小说"),
            CategoryEntity(3, "收藏")
        )
    }

    var selectedTab by remember { mutableIntStateOf(0) }

    CategoryTabRow(
        categories = categories,
        bookCounts = listOf(10, 20, 30),
        selectedIndex = selectedTab,
        onTabSelected = {
            selectedTab = it
        }
    )
}