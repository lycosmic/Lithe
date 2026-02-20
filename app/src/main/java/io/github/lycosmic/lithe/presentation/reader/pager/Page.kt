package io.github.lycosmic.lithe.presentation.reader.pager

import androidx.compose.ui.text.AnnotatedString
import io.github.lycosmic.lithe.presentation.reader.ReaderContent

/**
 * 代表一个完整的页面
 */
data class BookPage(
    val index: Int,
    val items: List<PageItem>
)

// 扩展方法，方便获取一页的起始字符位置
fun BookPage.getStartCharIndex(): Int {
    val firstItem = items.firstOrNull() ?: return 0
    return when (firstItem) {
        is PageItem.Whole -> firstItem.content.startIndex
        is PageItem.TextSplit -> firstItem.startCharIndex
    }
}

/**
 * 页面上的具体元素
 */
sealed interface PageItem {
    // 完整展示的元素 (图片、标题、完全容纳下的段落)
    data class Whole(
        val content: ReaderContent
    ) : PageItem

    // 被切割的段落或是完整的段落（是段落开头，也是段落结尾）
    data class TextSplit(
        val originContent: ReaderContent.Paragraph, // 原始数据
        val subText: AnnotatedString, // 这一页展示的子串
        val isStart: Boolean, // 是否是段落的开头 (用于首行缩进)
        val isEnd: Boolean,    // 是否是段落的结尾 (用于段后间距)
        val startCharIndex: Int, // 段落开头在本章中的起始位置
    ) : PageItem
}