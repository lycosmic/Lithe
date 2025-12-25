package io.github.lycosmic.lithe.presentation.browse.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * 圆形复选框
 */
@Composable
fun RoundCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.surface,
) {
    // 选中时的背景色
    val backgroundColor = if (checked) MaterialTheme.colorScheme.secondary else Color.Transparent
    // 未选中时的边框色
    val borderColor = if (checked) Color.Transparent else MaterialTheme.colorScheme.outline

    Box(
        modifier = modifier
            .size(24.dp)
            .clip(shape = CircleShape)
            .background(color = backgroundColor)
            .border(width = 2.dp, color = borderColor, shape = CircleShape)
            .clickable(enabled = onCheckedChange != null) {
                onCheckedChange?.invoke(!checked)
            },
        contentAlignment = Alignment.Center
    ) {
        // 显示对勾
        AnimatedVisibility(visible = checked, enter = fadeIn(), exit = fadeOut()) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(16.dp)
            )
        }
    }

}