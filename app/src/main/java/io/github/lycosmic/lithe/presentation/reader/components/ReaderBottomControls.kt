package io.github.lycosmic.lithe.presentation.reader.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ReaderBottomControls(
    bookProgress: Float,
    chapterProgress: Float,
    onProgressChange: (Float) -> Unit,
    currentChapterIndex: Int, // 当前第几章
    isPrevVisible: Boolean,
    isNextVisible: Boolean,
    onPrevClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val text = remember(bookProgress, chapterProgress) {
        "${bookProgress * 100}% (${chapterProgress * 100}%)"
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding() // 避开底部导航栏
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 进度百分比文字
            Text(text, style = MaterialTheme.typography.bodyLarge)

            Spacer(Modifier.height(8.dp))

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
                    value = bookProgress,
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