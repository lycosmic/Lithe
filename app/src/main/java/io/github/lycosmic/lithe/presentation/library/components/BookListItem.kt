package io.github.lycosmic.lithe.presentation.library.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.lycosmic.domain.model.Book
import io.github.lycosmic.lithe.ui.components.AsyncCoverImage
import io.github.lycosmic.lithe.ui.components.StyledText
import io.github.lycosmic.lithe.util.FormatUtils


@Composable
fun BookListItem(
    book: Book,
    isSelected: Boolean,
    onBookClick: () -> Unit,
    onBookLongClick: () -> Unit,
    onReadButtonClick: () -> Unit,
    showProgress: Boolean,
    showReadButton: Boolean,
    modifier: Modifier = Modifier
) {
    // 选中和未选中的背景色
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer
    else Color.Transparent

    // 选中和未选中的文字色
    val fontColor = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer
    else MaterialTheme.colorScheme.onSurface

    val progress = remember(book.progress) {
        FormatUtils.formatProgress(book.progress)
    }

    Row(
        modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(backgroundColor)
            .combinedClickable(
                onClick = onBookClick,
                onLongClick = onBookLongClick
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- 封面 ---
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            book.coverPath?.let {
                AsyncCoverImage(
                    path = it,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.small)
                )
            }

            if (book.coverPath == null) {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(0.7f)
                        .aspectRatio(1f),
                    tint = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // -- 标题 ---
        StyledText(
            text = book.title,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium.copy(
                color = fontColor
            ),
            maxLines = 2
        )

        Spacer(modifier = Modifier.width(8.dp))

        // --- 阅读进度 ---
        if (showProgress) {
            Spacer(modifier = Modifier.width(8.dp))

            StyledText(
                text = progress,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.tertiary)
                    .padding(horizontal = 6.dp, vertical = 3.dp),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onTertiary
                )
            )
        }

        // --- 阅读按钮 ---
        if (showReadButton) {
            Spacer(modifier = Modifier.width(8.dp))

            FilledIconButton(
                onClick = onReadButtonClick,
                modifier = Modifier.size(32.dp),
                shape = MaterialTheme.shapes.medium,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}