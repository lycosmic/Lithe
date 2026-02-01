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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import io.github.lycosmic.data.settings.AppTextAlign
import io.github.lycosmic.data.settings.ReaderFontWeight
import io.github.lycosmic.lithe.R


/**
 * 字体预览卡片
 *
 * @param font 字体
 * @param weight 字体粗细
 * @param fontSize 字体大小
 * @param isItalic 是否斜体
 * @param letterSpacing 字间距
 * @param appTextAlign 文本对齐方式
 * @param lineHeight 行高
 * @param paragraphIndent 段落缩进
 * @param defaultPreviewText 预览文本
 */
@Composable
fun FontPreviewCard(
    font: FontFamily,
    weight: ReaderFontWeight,
    fontSize: Int, // pt
    isItalic: Boolean,
    letterSpacing: Int, // pt
    appTextAlign: AppTextAlign,
    lineHeight: Int, // pt TODO: 传入倍数或增加基础行高逻辑
    paragraphIndent: Int, // pt
    modifier: Modifier = Modifier,
    defaultPreviewText: String = stringResource(R.string.reader_preview_text)
) {
    // 预览文本
    var previewText by remember(defaultPreviewText) {
        mutableStateOf(defaultPreviewText)
    }

    // 行高
    val calculatedLineHeight = (fontSize + lineHeight).sp

    // 字间距
    val calculatedLetterSpacing = (letterSpacing / 100f).em

    // 文字对齐方式
    val composeTextAlign = AppTextAlign.getTextAlign(appTextAlign)

    // 只有 START 和 JUSTIFY 时应用首行缩进
    val textIndent = remember(paragraphIndent, appTextAlign, fontSize) {
        if (appTextAlign == AppTextAlign.CENTER || appTextAlign == AppTextAlign.END) {
            TextIndent.None
        } else {
            TextIndent(firstLine = (paragraphIndent * 6).sp)
        }
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
                    fontFamily = font,
                    fontWeight = weight.weight,
                    fontSize = fontSize.sp,
                    fontStyle = if (isItalic) FontStyle.Italic else FontStyle.Normal,
                    lineHeight = calculatedLineHeight,
                    letterSpacing = calculatedLetterSpacing,
                    textAlign = composeTextAlign,
                    textIndent = textIndent,
                    color = colorResource(R.color.reader_preview_text_color),
                    lineHeightStyle = LineHeightStyle(
                        alignment = LineHeightStyle.Alignment.Center,
                        trim = LineHeightStyle.Trim.None
                    )
                ),
                modifier = Modifier.fillMaxWidth(),
                cursorBrush = SolidColor(colorResource(R.color.reader_preview_text_color))
            )
        }
    }
}