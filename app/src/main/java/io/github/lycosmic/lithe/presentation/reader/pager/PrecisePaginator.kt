package io.github.lycosmic.lithe.presentation.reader.pager


import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.lycosmic.lithe.presentation.reader.ReaderContent
import io.github.lycosmic.lithe.presentation.reader.model.ReaderStyle
import io.github.lycosmic.lithe.presentation.reader.pager.PageItem.TextSplit
import io.github.lycosmic.lithe.presentation.reader.pager.PageItem.Whole

object PrecisePaginator {

    fun paginate(
        contents: List<ReaderContent>,
        readerStyle: ReaderStyle,
        textMeasurer: TextMeasurer,
        density: Density,
        availableWidth: Int,  // 内容区域宽度 （像素）
        availableHeight: Int  // 内容区域高度
    ): List<BookPage> {
        val pages = mutableListOf<BookPage>()
        val currentPageItems = mutableListOf<PageItem>()
        var currentY = 0 // 当前页面已经使用的像素高度

        // 提交当前页的内容
        fun commitPage() {
            if (currentPageItems.isNotEmpty()) {
                pages.add(BookPage(pages.size, currentPageItems.toList()))
                currentPageItems.clear()
                currentY = 0
            }
        }

        val titleTopSpacingPx = with(density) { 22.dp.toPx().toInt() } // 标题上方留白
        val paragraphSpacingPx =
            with(density) { readerStyle.paragraphSpacing.toPx().toInt() } // 段间距
        val dividerHeightPx = with(density) { 32.dp.toPx().toInt() }   // 分割线高度

        // 遍历所有内容
        contents.forEach { content ->
            // 如果不是页面第一个元素，加上段间距
            var itemSpacing = if (currentPageItems.isNotEmpty()) paragraphSpacingPx else 0

            when (content) {
                // --- 处理标题 ---
                is ReaderContent.Title -> {
                    val measureResult = textMeasurer.measure(
                        text = content.text,
                        style = TextStyle(
                            fontSize = 28.sp, // TODO: 外部传入
                            fontFamily = readerStyle.fontFamily,
                        ),
                        constraints = Constraints(maxWidth = availableWidth)
                    )

                    val height = measureResult.size.height + titleTopSpacingPx

                    // 如果剩余空间不足，直接换页
                    if (currentY + height > availableHeight) {
                        commitPage()
                    }

                    currentPageItems.add(Whole(content))
                    currentY += height
                }


                // --- 处理分割线 ---
                is ReaderContent.Divider -> {
                    if (currentY + dividerHeightPx > availableHeight) {
                        commitPage()
                    }

                    currentPageItems.add(Whole(content))
                    currentY += dividerHeightPx
                }

                // --- 处理段落 ---
                is ReaderContent.Paragraph -> {
                    // 测量样式
                    val textStyle = TextStyle(
                        fontFamily = readerStyle.fontFamily,
                        fontSize = readerStyle.fontSize,
                        lineHeight = readerStyle.lineHeight,
                        letterSpacing = readerStyle.letterSpacing,
                        textIndent = TextIndent(firstLine = readerStyle.paragraphIndent),
                        textAlign = readerStyle.paragraphAlign
                    )

                    // 递归处理可能跨多页的长段落
                    fun layoutParagraph(
                        text: AnnotatedString,
                        isStart: Boolean
                    ) {
                        // 测量文字高度
                        val layoutResult = textMeasurer.measure(
                            text = text,
                            style = textStyle,
                            constraints = Constraints(maxWidth = availableWidth)
                        )

                        // 如果能完全放下
                        if (currentY + itemSpacing + layoutResult.size.height <= availableHeight) {
                            currentPageItems.add(
                                TextSplit(content, text, isStart, isEnd = true)
                            )
                            currentY += (itemSpacing + layoutResult.size.height)
                            return
                        }

                        // 放不下，计算当前页还剩多少高度
                        val remainingHeight = availableHeight - currentY

                        // 查找在哪个字符处断开
                        var splitOffset = 0
                        for (lineIndex in 0 until layoutResult.lineCount) {
                            if (layoutResult.getLineBottom(lineIndex) + itemSpacing > remainingHeight) {
                                // 这一行超出了剩余空间
                                if (lineIndex == 0) {
                                    // 连第一行都放不下 -> 强行换页
                                    // 不进行切割，直接把整个任务丢给下一页
                                    commitPage()
                                    itemSpacing = 0
                                    // 递归调用，在下一页重新尝试
                                    layoutParagraph(text, isStart)
                                    return
                                }

                                // 正常情况：在上一行结束处截断
                                splitOffset =
                                    layoutResult.getLineEnd(lineIndex - 1, visibleEnd = true)
                                break
                            }
                        }

                        // 如果没有找到分割点，也强制换页
                        if (splitOffset <= 0) {
                            commitPage()
                            itemSpacing = 0
                            layoutParagraph(text, isStart)
                            return
                        }

                        // 切割文字
                        val firstPart = text.subSequence(0, splitOffset)
                        val secondPart = text.subSequence(splitOffset, text.length)

                        // 添加第一部分到当前页
                        currentPageItems.add(
                            TextSplit(content, firstPart, isStart, isEnd = false)
                        )
                        commitPage() // 这一页满了，提交

                        // 递归处理剩下的部分
                        currentY = 0
                        itemSpacing = 0
                        layoutParagraph(secondPart, isStart = false)
                    }

                    layoutParagraph(content.text, isStart = true)
                }
                // --- 处理图片 ---
                is ReaderContent.Image -> {
                    // 计算图片在当前屏幕宽度下的高度
                    val imgWidth = availableWidth * readerStyle.imageWidthPercent
                    val imgHeight = (imgWidth / content.aspectRatio).toInt()

                    val totalHeightNeeded = itemSpacing + imgHeight

                    // 是否需要换页
                    if (currentY + totalHeightNeeded > availableHeight) {
                        commitPage() // 换页
                        itemSpacing = 0
                    }

                    currentPageItems.add(Whole(content))
                    currentY += totalHeightNeeded
                }

                is ReaderContent.ImageDescription -> {
                    val textStyle = TextStyle(
                        fontFamily = readerStyle.fontFamily,
                        fontSize = readerStyle.fontSize * 0.8f,
                        lineHeight = readerStyle.lineHeight,
                        letterSpacing = readerStyle.letterSpacing,
                        textIndent = TextIndent(firstLine = readerStyle.paragraphIndent),
                        textAlign = readerStyle.paragraphAlign
                    )

                    val measureResult = textMeasurer.measure(
                        text = content.caption,
                        style = textStyle,
                        constraints = Constraints(maxWidth = availableWidth)
                    )

                    val height = measureResult.size.height
                    if (currentY + height > availableHeight) {
                        commitPage() // 换页
                    }

                    currentPageItems.add(Whole(content))
                    currentY += height
                }
            }
        }

        // 提交最后一页
        commitPage()

        return pages
    }
}