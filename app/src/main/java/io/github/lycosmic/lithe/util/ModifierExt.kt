package io.github.lycosmic.lithe.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

/**
 * 无水波纹的点击
 */
@Composable
fun Modifier.clickableNoRipple(enabled: Boolean = true, onClick: () -> Unit): Modifier = this.then(
    this.clickable(
        interactionSource = remember { MutableInteractionSource() },
        onClick = onClick,
        indication = null, // 禁用水波纹
        enabled = enabled
    )
)