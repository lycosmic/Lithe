package io.github.lycosmic.lithe.presentation.reader.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.lycosmic.lithe.ui.components.StyledText

@Composable
fun ReaderBottomControls(
    progress: Float,
    progressText: String,
    onProgressChange: (Float) -> Unit,
    isPrevVisible: Boolean,
    isNextVisible: Boolean,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
    bottomBarPadding: Int
) {
    // 底部栏边距
    val bottomBarPadding = remember(bottomBarPadding) {
        (bottomBarPadding * 4f).dp
    }

    Surface(
        modifier = modifier
            .fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.9f),
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding() // 避开底部导航栏
                .padding(horizontal = 18.dp)
                .padding(top = 16.dp, bottom = 8.dp + bottomBarPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // 进度文本
            StyledText(
                text = progressText,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                )
            )

            // 进度条区域
            Row(verticalAlignment = Alignment.CenterVertically) {
                AnimatedVisibility(isPrevVisible) {
                    IconButton(onClick = onPrevClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
                Slider(
                    value = progress,
                    onValueChange = onProgressChange,
                    modifier = Modifier.weight(1f)
                )
                AnimatedVisibility(isNextVisible) {
                    IconButton(onClick = onNextClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }


}