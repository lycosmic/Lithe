package io.github.lycosmic.lithe.presentation.reader.components

import androidx.compose.foundation.MarqueeSpacing
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.lycosmic.lithe.R

@Composable
fun ReaderTopBar(
    bookName: String, // 书籍名称
    chapterName: String, // 章节名称
    chapterProgress: Float, // 章节进度，范围0.0-1.0
    onBackClick: () -> Unit,
    onChapterMenuClick: () -> Unit, // 章节菜单
    onReaderSettingsClick: () -> Unit, // 阅读设置
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f), // 半透明背景
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // 1. 顶部内容
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding() // 避开系统状态栏
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 返回按钮
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
                }

                Spacer(Modifier.width(8.dp))

                // 文本区域
                Column(Modifier.weight(1f)) {
                    // 书名，不滚动，溢出显示省略号
                    Text(
                        text = bookName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(2.dp))

                    // 章节名，水平无限滚动
                    Text(
                        text = chapterName,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.basicMarquee( // 走马灯
                            iterations = Int.MAX_VALUE,
                            spacing = MarqueeSpacing.fractionOfContainer(1f / 4f)
                        )
                    )
                }

                // 右侧操作按钮
                IconButton(onClick = onChapterMenuClick) {
                    Icon(Icons.Default.Menu, contentDescription = null)
                }
                IconButton(onClick = onReaderSettingsClick) {
                    Icon(Icons.Default.Settings, contentDescription = null)
                }
            }

            // 2.底部进度条
            LinearProgressIndicator(
                progress = {
                    chapterProgress.coerceIn(0f, 1f)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}