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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.lycosmic.domain.model.Category
import io.github.lycosmic.lithe.R

/**
 * 书库顶部的分类标签栏
 */
@Composable
fun CategoryTabRow(
    categories: List<Category>,
    bookCountsList: List<Int>,
    isBookCountVisible: Boolean,
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

            val categoryName =
                category.name.ifBlank { stringResource(id = R.string.default_category_name) }

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
                            text = categoryName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        if (isBookCountVisible) {
                            Spacer(modifier = Modifier.width(6.dp))

                            // 分类书籍数
                            CountBadge(
                                count = bookCountsList[index]
                            )
                        }
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
            Category(1, "默认"),
            Category(2, "轻小说"),
            Category(3, "收藏")
        )
    }

    var selectedTab by remember { mutableIntStateOf(0) }

    CategoryTabRow(
        categories = categories,
        bookCountsList = listOf(10, 20, 30),
        selectedIndex = selectedTab,
        onTabSelected = {
            selectedTab = it
        },
        isBookCountVisible = true
    )
}