package io.github.lycosmic.lithe.presentation.reader.components


import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.sp
import io.github.lycosmic.lithe.presentation.reader.model.ReaderStyle
import io.github.lycosmic.lithe.presentation.reader.pager.BookPage
import io.github.lycosmic.lithe.presentation.reader.pager.PageItem
import io.github.lycosmic.lithe.ui.components.StyledText

/**
 * 专门负责渲染“某一页”的内容
 */
@Composable
fun PageRenderer(
    page: BookPage,
    pagerState: PagerState,
    readerStyle: ReaderStyle,
    onContentClick: () -> Unit,
    onPageChange: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
//            .background(Color.Green.copy(alpha = 0.1f))
            .pointerInput(Unit) { // 获取点击坐标
                detectTapGestures { offset ->
                    val width = size.width
                    val x = offset.x

                    when {
                        x < width * 0.3f -> {
                            // 点击左侧 -> 上一页
                            val prevPage = (pagerState.currentPage - 1).coerceAtLeast(0)
                            onPageChange(prevPage)
                        }

                        x > width * 0.7f -> {
                            // 点击右侧 -> 下一页
                            val nextPage =
                                (pagerState.currentPage + 1).coerceAtMost(pagerState.pageCount - 1)
                            onPageChange(nextPage)
                        }

                        else -> {
                            // 点击中间 -> 弹出菜单
                            onContentClick()
                        }
                    }
                }
            }
    ) {
        page.items.forEachIndexed { index, item ->
            // 元素之间的段落间距
            if (index > 0) {
                Spacer(modifier = Modifier.height(readerStyle.paragraphSpacing))
            }

            when (item) {
                // 完整的元素
                is PageItem.Whole -> {
                    ReaderItemRenderer(
                        content = item.content,
                        style = readerStyle
                    )
                }

                // 被切开的段落
                is PageItem.TextSplit -> {
                    // 只有当这是段落的开头部分时，才应用首行缩进
                    val indent = if (item.isStart) readerStyle.paragraphIndent else 0.sp

                    StyledText(
                        text = item.subText,
                        style = TextStyle(
                            fontFamily = readerStyle.fontFamily,
                            fontStyle = readerStyle.fontStyle,
                            fontWeight = readerStyle.fontWeight,
                            fontSize = readerStyle.fontSize,
                            lineHeight = readerStyle.lineHeight,
                            letterSpacing = readerStyle.letterSpacing,
                            textAlign = readerStyle.paragraphAlign,
                            color = readerStyle.textColor,
                            textIndent = TextIndent(firstLine = indent),
                            lineBreak = LineBreak.Paragraph,
                            platformStyle = PlatformTextStyle(
                                includeFontPadding = false
                            ),
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = readerStyle.sidePadding)
//                            .background(Color.Red.copy(alpha = 0.2f))
                    )
                }
            }
        }
    }
}