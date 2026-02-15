package io.github.lycosmic.lithe.presentation.library.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.lycosmic.lithe.ui.components.AsyncCoverImage
import io.github.lycosmic.lithe.ui.theme.LitheTheme
import io.github.lycosmic.lithe.util.FormatUtils.formatProgress


@Composable
fun BookGridItem(
    title: String,
    modifier: Modifier = Modifier,
    coverPath: String? = null,
    readProgress: Float,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onReadButtonClick: () -> Unit
) {
    // 容器颜色
    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.secondary
    } else {
        Color.Transparent
    }

    // 标题颜色
    val titleColor = if (isSelected) {
        MaterialTheme.colorScheme.onSecondary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    // 圆角
    val coverShape = RoundedCornerShape(size = 14.dp)
    val containerShape = RoundedCornerShape(size = 16.dp)

    val progressText = remember(readProgress) {
        formatProgress(readProgress)
    }

    Column(
        modifier = modifier
            .clip(shape = containerShape)
            .background(color = containerColor)
            .padding(all = 3.dp)
    ) {
        // 封面
        Surface(
            modifier = Modifier
                .aspectRatio(0.7f) // 标准书籍比例 (1:1.4左右)
                .fillMaxWidth()
                .clip(shape = coverShape)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = onLongClick
                ),
            color = MaterialTheme.colorScheme.surfaceContainerLow,
            shape = coverShape,
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (coverPath != null) { // 有封面图
                    AsyncCoverImage(
                        path = coverPath,
                        contentDescription = null,
                        modifier = Modifier
                    )
                } else {
                    // 图片图标占位符
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

                // 阅读进度
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 4.dp, start = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.tertiary)
                        .padding(vertical = 4.dp, horizontal = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = progressText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiary
                    )
                }

                // 开始阅读按钮
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color = MaterialTheme.colorScheme.primaryContainer)
                        .clickable {
                            onReadButtonClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.padding(4.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 书名
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = titleColor,
            modifier = Modifier.padding(start = 4.dp, end = 4.dp, bottom = 4.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun BookGridItemPreview() {
    LitheTheme {
        BookGridItem(
            title = "《三体》",
            coverPath = null,
            readProgress = 0.5f,
            isSelected = true,
            onClick = {},
            onLongClick = {},
            onReadButtonClick = {}
        )
    }
}