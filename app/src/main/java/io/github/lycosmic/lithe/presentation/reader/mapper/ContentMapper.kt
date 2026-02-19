package io.github.lycosmic.lithe.presentation.reader.mapper

import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import io.github.lycosmic.domain.model.BookContentBlock
import io.github.lycosmic.domain.model.StyleType
import io.github.lycosmic.lithe.presentation.reader.ReaderContent
import io.github.lycosmic.lithe.presentation.reader.ReaderContent.Divider
import io.github.lycosmic.lithe.presentation.reader.ReaderContent.Image
import io.github.lycosmic.lithe.presentation.reader.ReaderContent.ImageDescription
import io.github.lycosmic.lithe.presentation.reader.ReaderContent.Paragraph
import io.github.lycosmic.lithe.presentation.reader.ReaderContent.Title
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


object ContentMapper {
    suspend fun mapToUi(chapterIndex: Int, bookContentBlock: BookContentBlock): ReaderContent =
        withContext(Dispatchers.Default) {
            return@withContext when (bookContentBlock) {
                is BookContentBlock.Divider -> {
                    Divider(
                        startIndex = bookContentBlock.startIndex,
                        chapterIndex = chapterIndex
                    )
                }

                is BookContentBlock.Image -> {
                    Image(
                        startIndex = bookContentBlock.startIndex,
                        chapterIndex = chapterIndex,
                        path = bookContentBlock.path,
                        aspectRatio = bookContentBlock.aspectRatio
                    )
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

                    Paragraph(
                        startIndex = bookContentBlock.startIndex,
                        chapterIndex = chapterIndex,
                        text = styledText
                    )
                }

                is BookContentBlock.Title -> {
                    Title(
                        startIndex = bookContentBlock.startIndex,
                        chapterIndex = chapterIndex,
                        text = bookContentBlock.text,
                        level = bookContentBlock.level
                    )
                }

                is BookContentBlock.ImageAlt -> {
                    ImageDescription(
                        startIndex = bookContentBlock.startIndex,
                        chapterIndex = chapterIndex,
                        caption = bookContentBlock.caption
                    )
                }
            }
        }
}