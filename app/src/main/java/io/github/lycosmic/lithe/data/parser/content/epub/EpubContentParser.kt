package io.github.lycosmic.lithe.data.parser.content.epub

import android.content.Context
import android.net.Uri
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import io.github.lycosmic.lithe.data.model.content.ReaderContent
import io.github.lycosmic.lithe.data.parser.content.BookContentParser
import io.github.lycosmic.lithe.utils.ZipUtils
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EpubContentParser @Inject constructor() : BookContentParser {


    /**
     * 解析书籍内容
     * 利用 Jsoup 把 HTML 转成我们的 ReaderContent
     */
    override suspend fun parseChapterContent(
        context: Context,
        bookUri: Uri,
        chapterHref: String
    ): List<ReaderContent> {
        val contentList = mutableListOf<ReaderContent>()

        ZipUtils.findZipEntryAndAction(context, bookUri, chapterHref) { inputStream ->
            // 使用 Jsoup 解析 HTML 流
            val doc = Jsoup.parse(inputStream, Charsets.UTF_8.toString(), chapterHref)
            val body = doc.body()

            // 递归遍历 HTML 节点
            parseHtmlNode(context, bookUri, body, contentList, chapterHref)
        }

        return contentList
    }

    /**
     * 将 HTML 节点转为 ReaderContent
     */
    private suspend fun parseHtmlNode(
        context: Context,
        bookUri: Uri,
        element: Element,
        list: MutableList<ReaderContent>,
        currentChapterPath: String
    ) {
        for (node in element.childNodes()) {
            if (node is TextNode) {
                // 纯文本节点
                val text = node.text().replace('\u00A0', ' ').trim()
                if (text.isNotEmpty()) {
                    // 不是空白符,作为段落
                    list.add(ReaderContent.Paragraph(AnnotatedString(text)))
                }
            } else if (node is Element) {
                when (node.tagName()) {
                    "h1", "h2", "h3", "h4", "h5", "h6" -> {
                        val text = node.text().trim()
                        list.add(
                            ReaderContent.Title(
                                text,
                                level = node.tagName()[1].digitToInt()
                            )
                        )
                    }

                    "p", "div" -> {
                        // 解析段落
                        val styledText = parseStyledText(node)
                        if (styledText.text.trim().isNotEmpty()) {
                            list.add(ReaderContent.Paragraph(styledText))
                        } else {
                            // 继续递归
                            parseHtmlNode(context, bookUri, node, list, currentChapterPath)
                        }
                    }

                    "img", "image" -> {
                        // 提取图片路径
                        var src = node.attr("src")
                        if (src.isEmpty()) src = node.attr("href")
                        if (src.isEmpty()) src = node.attr("xlink:href")

                        // 如果当前节点是 SVG,则递归处理子元素
                        if (node.tagName() == "svg") {
                            parseHtmlNode(context, bookUri, node, list, currentChapterPath)
                            return
                        }

                        if (src.isNotEmpty()) {
                            // 处理相对路径
                            val relativePath = resolveRelativePath(currentChapterPath, src)

                            // 将图片提取到缓存
                            val cachedPath =
                                ZipUtils.extractCoverToCache(context, bookUri, relativePath)
                            if (cachedPath != null) {
                                list.add(ReaderContent.Image(relativePath = cachedPath))
                            }
                        }
                    }

                    "hr" -> {
                        list.add(ReaderContent.Divider)
                    }

                    else -> {
                        // 递归处理子元素
                        parseHtmlNode(context, bookUri, node, list, currentChapterPath)
                    }
                }
            }
        }
    }


    /**
     * 解析带样式的文本 (Bold, Italic)
     * 将 Jsoup Element 转为 Compose AnnotatedString
     */
    private fun parseStyledText(element: Element): AnnotatedString {
        return buildAnnotatedString {
            // 递归遍历段落内部的子节点
            fun appendNode(node: org.jsoup.nodes.Node) {
                if (node is TextNode) {
                    append(node.text())
                } else if (node is Element) {
                    when (node.tagName()) {
                        "b", "strong" -> {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                node.childNodes().forEach { appendNode(it) }
                            }
                        }

                        "i", "em" -> {
                            withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                                node.childNodes().forEach { appendNode(it) }
                            }
                        }

                        "br" -> append("\n")
                        else -> {
                            node.childNodes().forEach { appendNode(it) }
                        }
                    }
                }
            }

            element.childNodes().forEach { appendNode(it) }
        }
    }

    // 简单的相对路径计算
    private fun resolveRelativePath(baseFile: String, relativeUrl: String): String {
        // baseFile: OEBPS/Text/chap1.html
        // relativeUrl: ../Images/1.jpg

        try {
            // OEBPS/Text
            val baseDir = if (baseFile.contains("/")) baseFile.substringBeforeLast("/") else ""

            val fakeRoot = java.net.URI.create("file:///root/")
            val baseUri = fakeRoot.resolve("$baseDir/") // 注意结尾斜杠
            val resolved = baseUri.resolve(relativeUrl)

            return resolved.path.removePrefix("/root/")
        } catch (_: Exception) {
            return relativeUrl // 失败降级
        }
    }
}