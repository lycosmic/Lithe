package io.github.lycosmic.lithe.presentation.library.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.lycosmic.lithe.ui.components.StyledText


/**
 * 计数标签组件 - 用于显示分类数或书籍数
 */
@Composable
fun CountBadge(
    count: Int,
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    badgeColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surfaceContainer,
    textColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant,
    badgeShape: androidx.compose.foundation.shape.CornerBasedShape = RoundedCornerShape(14.dp),
    badgePaddings: PaddingValues = PaddingValues(horizontal = 8.dp, vertical = 3.dp),
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium.copy(
        fontSize = 16.sp,
        color = textColor
    )
) {
    if (visible) {
        StyledText(
            text = count.toString(),
            modifier = modifier
                .background(
                    badgeColor,
                    badgeShape
                )
                .padding(badgePaddings),
            style = textStyle
        )
    }
}