package io.github.lycosmic.lithe.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import io.github.lycosmic.lithe.R
import io.github.lycosmic.lithe.data.local.entity.CategoryEntity
import io.github.lycosmic.lithe.ui.theme.LitheTheme


/**
 * 移动书籍分类对话框
 */
@Composable
fun MoveBookCategoryDialog(
    selectedCategoryIds: List<Long>, // 当前已选中的分类ID
    categories: List<CategoryEntity>, // 分类列表
    onDismissRequest: () -> Unit,
    onConfirm: (List<Long>) -> Unit,
    onEdit: () -> Unit, // 点击编辑按钮
    modifier: Modifier = Modifier,
) {
    // 当前已选中的分类ID
    val selectedIds = remember(selectedCategoryIds) {
        mutableStateListOf<Long>().apply {
            addAll(selectedCategoryIds)
        }
    }

    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
            modifier = modifier
                .fillMaxWidth()
        ) {
            // 内容
            Column(modifier = Modifier.padding(vertical = 16.dp)) {
                // 标题
                StyledText(
                    text = stringResource(id = R.string.move_book_category_dialog_title),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 选项列表
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp) // 限制最大高度
                ) {
                    items(categories) { option ->
                        val isSelected = selectedIds.contains(option.id)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clickable {
                                    if (isSelected) selectedIds.remove(option.id)
                                    else selectedIds.add(option.id)
                                }
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = null
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = option.name, fontSize = 18.sp)
                        }
                    }
                }

                // --- 无分类提示 ---
                if (categories.isEmpty()) {
                    StyledText(
                        text = stringResource(id = R.string.move_book_category_dialog_no_category),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                // 底部按钮栏
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onEdit) {
                        StyledText(
                            text = stringResource(id = R.string.move_book_category_dialog_edit_button),
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    TextButton(onClick = onDismissRequest) {
                        StyledText(
                            text = stringResource(id = R.string.move_book_category_dialog_cancel_button),
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                    TextButton(onClick = { onConfirm(selectedIds.toList()) }) {
                        StyledText(
                            text = stringResource(id = R.string.move_book_category_dialog_confirm_button),
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }

            }
        }
    }
}

@Preview
@Composable
private fun MoveBookCategoryDialogPreview() {
    LitheTheme {
        MoveBookCategoryDialog(
            selectedCategoryIds = listOf(1),
            categories = listOf(CategoryEntity(1, "分类1"), CategoryEntity(2, "分类2")),
            onDismissRequest = {},
            onConfirm = {},
            onEdit = {}
        )
    }
}