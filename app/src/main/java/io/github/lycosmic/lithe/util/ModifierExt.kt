package io.github.lycosmic.lithe.util

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

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


/**
 * 水平滑动切换
 */
@Composable
fun Modifier.swipeTrigger(
    enabled: Boolean = true,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    thresholdPx: Float = 100f // 触发阈值
): Modifier {
    // 当前拖动距离
    var dragOffset by remember { mutableFloatStateOf(0f) }
    // 是否已触发切换
    var hasTriggered by remember { mutableStateOf(false) }

    return this.pointerInput(Unit) {
        detectHorizontalDragGestures(
            onDragStart = {
                dragOffset = 0f
                hasTriggered = false
            },
            onDragEnd = {
                dragOffset = 0f
                hasTriggered = false
            }
        ) { change, dragAmount ->
            change.consume()
            if (!hasTriggered && enabled) {
                dragOffset += dragAmount

                when {
                    dragOffset > thresholdPx -> {
                        onSwipeRight()
                        hasTriggered = true
                    }

                    dragOffset < -thresholdPx -> {
                        onSwipeLeft()
                        hasTriggered = true
                    }
                }
            }
        }
    }
}