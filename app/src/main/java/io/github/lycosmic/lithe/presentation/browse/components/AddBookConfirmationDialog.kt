package io.github.lycosmic.lithe.presentation.browse.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.lycosmic.lithe.presentation.browse.model.ParsedBook
import io.github.lycosmic.lithe.ui.components.CircularWavyProgressIndicator


@Composable
fun AddBookConfirmationDialog(
    showDialog: Boolean,
    isDialogLoading: Boolean,
    selectedBooks: List<ParsedBook>,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onToggleSelection: (ParsedBook) -> Unit, // 向上传递当前点击的选项
    modifier: Modifier = Modifier
) {
    if (!showDialog) return
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text("取消")
            }
        },
        icon = {
            Icon(imageVector = Icons.Outlined.AddChart, contentDescription = null)
        },
        title = { Text("添加书籍？") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "您可以挑选您想添加哪些书。您可以在之后编辑书的标题与封面图片。加载过程可能会花费数分钟。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 书籍列表
                LazyColumn(
                    modifier = Modifier.weight(1f, fill = false), // 内容少时自适应，内容多时滚动
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (isDialogLoading) {
                        item {
                            // 使用自定义的波浪进度条
                            CircularWavyProgressIndicator()
                        }
                    } else {
                        items(
                            items = selectedBooks,
                            key = { it.file.uri.toString() }) { bookToAdd ->
                            BookToAddItem(
                                title = bookToAdd.metadata.title ?: bookToAdd.file.name,
                                author = bookToAdd.metadata.authors?.joinToString(", ") ?: "未知",
                                isSelected = bookToAdd.isSelected,
                                onItemClicked = { onToggleSelection(bookToAdd) },
                                onCheckboxClicked = { onToggleSelection(bookToAdd) }
                            )
                        }
                    }

                }
            }
        },
    )
}

@Composable
private fun BookToAddItem(
    title: String,
    author: String,
    isSelected: Boolean,
    onItemClicked: () -> Unit,
    onCheckboxClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.surfaceContainerHigh
    } else {
        Color.Transparent
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable {
                onItemClicked()
            }
            .padding(horizontal = 12.dp, vertical = 12.dp),
    ) {
        // 书名和作者
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1
            )
            Text(
                text = author,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        RoundCheckbox(checked = isSelected, onCheckedChange = {
            onCheckboxClicked()
        })
    }
}