package io.github.lycosmic.lithe.presentation.settings.library.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.lycosmic.domain.model.Category


/**
 * 分类列表
 */
@Composable
fun CategoryList(
    categories: List<Category>,
    onEditClick: (Category) -> Unit,     // 点击编辑图标
    onDeleteClick: (Category) -> Unit    // 点击删除图标
) {
    if (categories.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            categories.forEach { category ->
                CategoryListItem(
                    category = category,
                    onEditClick = { onEditClick(category) },
                    onDeleteClick = { onDeleteClick(category) }
                )
            }
        }
    }

}

/**
 * 分类项
 */
@Composable
fun CategoryListItem(
    category: Category,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    ListItem(
        // 左侧带圆形背景的图标
        leadingContent = {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Label,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },

        // 标题文字
        headlineContent = {
            Text(
                text = category.name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },

        // 右侧操作按钮组
        trailingContent = {
            Row {
                // 编辑按钮
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Outlined.EditNote,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                // 删除按钮
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },

        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent
        )
    )
}