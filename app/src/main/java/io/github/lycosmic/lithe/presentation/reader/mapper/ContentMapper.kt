package io.github.lycosmic.lithe.presentation.reader.mapper

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import io.github.lycosmic.lithe.presentation.reader.model.ReaderContent
import io.github.lycosmic.model.BookContentBlock
import io.github.lycosmic.model.StyleType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


object ContentMapper {
    suspend fun mapToUi(bookContentBlock: BookContentBlock): ReaderContent =
        withContext(Dispatchers.Default) {
            return@withContext when (bookContentBlock) {
                BookContentBlock.Divider -> {
                    ReaderContent.Divider
                }

                is BookContentBlock.Image -> {
                    ReaderContent.Image(bookContentBlock.path, bookContentBlock.path)
                }

                is BookContentBlock.Paragraph -> {
                    val styledText = buildAnnotatedString {
                        // 完整原始文本
                        append(bookContentBlock.text)

                        // 遍历样式列表，添加样式
                        bookContentBlock.styles.forEach { (start, end, type) ->
                            if (start >= 0 && end <= bookContentBlock.text.length && start < end) {
                                val spanStyle = when (type) {
                                    StyleType.BOLD -> SpanStyle(fontWeight = FontWeight.Bold)
                                    StyleType.ITALIC -> SpanStyle(fontStyle = FontStyle.Italic)
                                }

                                addStyle(
                                    style = spanStyle,
                                    start = start,
                                    end = end
                                )
                            }
                        }
                    }

                    ReaderContent.Paragraph(styledText)
                }

                is BookContentBlock.Title -> {
                    ReaderContent.Title(bookContentBlock.text, bookContentBlock.level)
                }
            }
        }
}