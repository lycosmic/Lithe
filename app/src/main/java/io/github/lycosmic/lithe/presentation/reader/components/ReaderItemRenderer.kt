package io.github.lycosmic.lithe.presentation.reader.components


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import io.github.lycosmic.lithe.presentation.reader.ReaderContent
import io.github.lycosmic.lithe.presentation.reader.model.ReaderStyle
import io.github.lycosmic.lithe.ui.components.StyledText


/**
 * 阅读器内容渲染器
 */
@Composable
fun ReaderItemRenderer(
    content: ReaderContent,
    style: ReaderStyle,
    modifier: Modifier = Modifier
) {
    // 通用 Modifier
    val commonModifier = modifier
        .fillMaxWidth()

    when (content) {
        is ReaderContent.Title -> {
            Column(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.height(22.dp))
                StyledText(
                    text = buildAnnotatedString { append(content.text) },
                    modifier = commonModifier,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = style.textColor,
                        textAlign = style.titleAlign
                    )
                )
            }
        }

        is ReaderContent.Paragraph -> {
            Column(
                modifier = commonModifier,
                horizontalAlignment = style.paragraphHorizontalAlign
            ) {
                StyledText(
                    text = content.text,
                    style = TextStyle(
                        fontFamily = style.fontFamily,
                        fontWeight = style.fontWeight,
                        textAlign = style.paragraphAlign,
                        textIndent = TextIndent(firstLine = style.paragraphIndent),
                        fontStyle = style.fontStyle,
                        letterSpacing = style.letterSpacing,
                        fontSize = style.fontSize,
                        lineHeight = style.lineHeight,
                        color = style.textColor,
                        lineBreak = LineBreak.Paragraph
                    )
                )
            }
        }

        is ReaderContent.Image -> {
            Box(
                modifier = commonModifier,
                contentAlignment = style.imageAlign
            ) {
                AsyncImage(
                    model = content.path,
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .clip(RoundedCornerShape(style.imageCornerRadius))
                        .fillMaxWidth(style.imageWidthPercent),
                    colorFilter = style.imageColorFilter,
                )
            }
        }

        is ReaderContent.Divider -> {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = style.textColor.copy(alpha = 0.4f)
            )
        }

        is ReaderContent.ImageDescription -> {
            Column(
                modifier = commonModifier,
                horizontalAlignment = style.paragraphHorizontalAlign
            ) {
                StyledText(
                    text = content.caption,
                    style = TextStyle(
                        fontFamily = style.fontFamily,
                        fontWeight = style.fontWeight,
                        textAlign = style.paragraphAlign,
                        fontSize = style.fontSize * 0.8f,
                        lineHeight = style.lineHeight,
                        color = style.textColor.copy(alpha = 0.8f),
                    )
                )
            }
        }
    }
}