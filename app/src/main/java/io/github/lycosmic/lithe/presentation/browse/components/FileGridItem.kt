package io.github.lycosmic.lithe.presentation.browse.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.lycosmic.data.util.FormatUtils
import io.github.lycosmic.model.FileItem


/**
 * 文件网格项
 */
@Composable
fun FileGridItem(
    fileItem: FileItem, // 文件项
    isSelected: Boolean, // 是否被选中
    isMultiSelectMode: Boolean,// 当前是否为多选模式
    onCheckboxClick: (Boolean) -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedBackgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    } else {
        Color.Transparent
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color = selectedBackgroundColor)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(4.dp)
    ) {
        // --- 文件图标 ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f) // 宽高一致
                .clip(RoundedCornerShape(8.dp))
                .border(
                    1.dp,
                    if (isSelected) MaterialTheme.colorScheme.outline
                    else MaterialTheme.colorScheme.outlineVariant,
                    RoundedCornerShape(8.dp)
                )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.InsertDriveFile,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .fillMaxWidth(0.3f)
                    .aspectRatio(1f)
                    .align(Alignment.Center)
            )

            // --- 文件选中框 ---
            this@Column.AnimatedVisibility(
                visible = isMultiSelectMode || isSelected,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
            ) {
                RoundCheckbox(
                    checked = isSelected,
                    onCheckedChange = onCheckboxClick,
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // --- 文件名称 ---
        Text(
            text = fileItem.name,
            style = MaterialTheme.typography.bodyLarge.copy(
                lineBreak = LineBreak.Simple
            ),
            overflow = TextOverflow.Clip,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(4.dp))
        // --- 文件大小和时间 ---
        Text(
            text = "${FormatUtils.formatFileSize(fileItem.size)}, ${
                FormatUtils.formatDate(
                    timeStamp = fileItem.lastModified, pattern = "dd MM月 yyyy"
                )
            }",
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}