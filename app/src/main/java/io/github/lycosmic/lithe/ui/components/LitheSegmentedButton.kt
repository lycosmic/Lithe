package io.github.lycosmic.lithe.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButtonColors
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.lycosmic.model.OptionItem

@Composable
fun <T> LitheSegmentedButton(
    modifier: Modifier = Modifier,
    items: List<OptionItem<T>>,
    onClick: (T) -> Unit
) {
    val itemSize = items.size

    // 外层容器：负责绘制整体的边框和圆角
    Row(
        modifier = modifier
            .clip(CircleShape)
            .border(
                width = 0.5.dp,
                color = SegmentedButtonDefaults.colors().activeBorderColor,
                shape = CircleShape
            )
            .padding(0.5.dp)
    ) {
        items.forEachIndexed { index, item ->
            // 缓存 Shape 对象，避免每次重组时都重新创建
            val shape = remember(index, itemSize) {
                itemShape(index, itemSize)
            }

            SingleSegmentedButton(
                item = item,
                shape = shape,
                onClick = { onClick(item.value) }
            )
        }
    }
}

@Composable
private fun <T> SingleSegmentedButton(
    item: OptionItem<T>,
    shape: Shape,
    colors: SegmentedButtonColors = SegmentedButtonDefaults.colors(),
    onClick: () -> Unit
) {
    Row(
        Modifier
            .height(40.dp)
            .clip(shape)
            .clickable(onClick = onClick)
            .border(
                width = 0.5.dp,
                color = colors.activeBorderColor,
                shape = shape
            )
            .padding(0.5.dp)
            .background( // 选中状态下的背景色
                if (item.selected) colors.activeContainerColor
                else Color.Transparent,
                shape = shape
            )
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedVisibility(
            visible = item.selected,
            enter = fadeIn() + expandHorizontally() + scaleIn() + slideInVertically(initialOffsetY = { it / 2 }),
            exit = fadeOut() + shrinkHorizontally() + scaleOut() + slideOutVertically(targetOffsetY = { it / 2 })
        ) {
            Row {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = null,
                    modifier = Modifier
                        .size(18.dp),
                    tint = colors.activeContentColor
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        Text(
            text = item.label,
            style = MaterialTheme.typography.labelLarge.copy(
                color = if (item.selected) colors.activeContentColor
                else colors.inactiveContentColor
            )
        )
    }
}

/**
 * 根据索引和总数计算按钮的形状
 */
private fun itemShape(index: Int, count: Int): Shape {
    if (count == 1) {
        return CircleShape
    }
    return when (index) {
        // 第一个：起始端（左侧）圆角
        0 -> RoundedCornerShape(topStartPercent = 50, bottomStartPercent = 50)
        // 最后一个：末端（右侧）圆角
        count - 1 -> RoundedCornerShape(topEndPercent = 50, bottomEndPercent = 50)
        // 中间：直角
        else -> RoundedCornerShape(0)
    }
}

@Preview(showBackground = true)
@Composable
private fun LitheSegmentedButtonPreview() {
    var selectedIndex by remember { mutableIntStateOf(1) }
    val items = listOf(
        OptionItem(0, "Day", selected = selectedIndex == 0),
        OptionItem(1, "Month", selected = selectedIndex == 1),
        OptionItem(2, "Week", selected = selectedIndex == 2),
    )
    Box(modifier = Modifier.fillMaxSize()) {
        LitheSegmentedButton(
            items = items,
            onClick = {
                selectedIndex = it
            },
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
