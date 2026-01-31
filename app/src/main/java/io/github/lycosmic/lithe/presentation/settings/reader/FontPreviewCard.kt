package io.github.lycosmic.lithe.presentation.settings.reader

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.lycosmic.data.settings.ReaderFontFamily
import io.github.lycosmic.data.settings.ReaderFontWeight
import io.github.lycosmic.lithe.R


/**
 * 字体预览卡片
 *
 * @param font 字体
 * @param weight 字体粗细
 * @param fontSize 字体大小
 * @param isItalic 是否斜体
 * @param defaultPreviewText 预览文本
 */
@Composable
fun FontPreviewCard(
    font: ReaderFontFamily,
    weight: ReaderFontWeight,
    fontSize: Float,
    isItalic: Boolean,
    letterSpacing: Float,
    modifier: Modifier = Modifier,
    defaultPreviewText: String = stringResource(R.string.reader_preview_text)
) {
    // 预览文本
    var previewText by remember(defaultPreviewText) {
        mutableStateOf(defaultPreviewText)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.reader_preview_background) // 模拟纸张的颜色
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 顶部栏：标题与重置按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.reader_preview_text_hint),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                IconButton(onClick = {
                    previewText = defaultPreviewText
                }, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = stringResource(R.string.reader_preview_reset_text),
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // TODO: 使用AnimatedContent进行动画过渡
            BasicTextField(
                value = previewText,
                onValueChange = {
                    previewText = it
                },
                textStyle = TextStyle(
                    fontFamily = font.family,
                    fontWeight = weight.weight,
                    fontSize = fontSize.sp,
                    fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                    lineHeight = (fontSize * 1.6).sp,
                    letterSpacing = letterSpacing.sp,
                    textAlign = TextAlign.Start,
                    color = colorResource(R.color.reader_preview_text_color)
                ),
                modifier = Modifier.fillMaxWidth(),
                cursorBrush = SolidColor(colorResource(R.color.reader_preview_text_color))
            )
        }
    }
}