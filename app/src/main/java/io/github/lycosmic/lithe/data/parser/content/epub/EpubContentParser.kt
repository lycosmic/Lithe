package io.github.lycosmic.lithe.data.parser.content.epub

import android.content.Context
import android.net.Uri
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.lycosmic.lithe.data.model.ReaderContent
import io.github.lycosmic.lithe.data.parser.content.BookContentParser
import io.github.lycosmic.lithe.extension.logD
import io.github.lycosmic.lithe.extension.logE
import io.github.lycosmic.lithe.extension.logI
import io.github.lycosmic.lithe.extension.logV
import io.github.lycosmic.lithe.utils.ZipUtils
import io.github.lycosmic.lithe.utils.ZipUtils.extractCoverToCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Comment
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EpubContentParser @Inject constructor(
    @param:ApplicationContext
    private val context: Context
) : BookContentParser {


    /**
     * 使用 Jsoup 解析书籍章节内容(HTML)
     * @param chapterPathOrHref 章节ZIP路径，例如 OEBPS/Text/chap1.html
     */
    override suspend fun parseChapterContent(
        bookUri: Uri,
        chapterPathOrHref: String
    ): List<ReaderContent> = withContext(Dispatchers.IO) {
        logI {
            "Start parsing the chapters, chapter path is $chapterPathOrHref"
        }
        val readerContentList = mutableListOf<ReaderContent>()

        ZipUtils.findZipEntryAndAction(
            context,
            bookUri,
            chapterPathOrHref
        ) { inputStream ->
            // 使用 Jsoup 解析章节 HTML
            val doc = Jsoup.parse(inputStream, Charsets.UTF_8.toString(), chapterPathOrHref)
            val body = doc.body()

            // 递归遍历 HTML 节点
            val readerContents = mutableListOf<ReaderContent>()
            // 传入累加器
            traverseHtmlElement(bookUri, body, readerContents, chapterPathOrHref)
            logD {
                "The parsed reading content is $readerContents"
            }
            readerContentList.addAll(readerContents)
        }

        logI {
            "The chapter content is parsed, and a total of ${readerContentList.size} elements are completed"
        }
        return@withContext readerContentList
    }


    /**
     * 串行递归遍历 HTML 节点
     * @param element 当前元素
     * @param accumulator 累加器
     */
    private suspend fun traverseHtmlElement(
        bookUri: Uri,
        element: Element,
        accumulator: MutableList<ReaderContent>,
        chapterRelativePath: String
    ): Unit = withContext(Dispatchers.Default) {

        for (node in element.childNodes()) {
            when (node) {
                is TextNode -> {
                    // 纯文本节点
                    val text = node.text().trim()
                    if (text.isNotEmpty()) {
                        logV {
                            "Encountered a text node: $text"
                        }
                        // 段落
                        accumulator.add(ReaderContent.Paragraph(AnnotatedString(text)))
                    } else {
                        logV {
                            "Encountered a blank text node"
                        }
                    }
                }

                is Comment -> {
                    // 忽略注释
                    logV {
                        "Encountered a comment node: $node"
                    }
                }

                is Element -> {
                    when (node.tagName()) {
                        "h1", "h2", "h3", "h4", "h5", "h6" -> {
                            logV {
                                "Encountered a heading tag: ${node.tagName()}"
                            }

                            val title = node.text().trim()
                            if (title.isNotEmpty()) {
                                accumulator.add(
                                    ReaderContent.Title(
                                        text = title,
                                        level = node.tagName().substring(1).toInt()
                                    )
                                )

                                accumulator.add(ReaderContent.Divider)
                            } else {
                                traverseHtmlElement(bookUri, node, accumulator, chapterRelativePath)
                            }
                        }

                        "p" -> {
                            logV {
                                "Encountered a paragraph tag"
                            }

                            // 解析段落
                            val styledText = parseStyledText(node)
                            if (styledText.text.trim().isNotEmpty()) {
                                accumulator.add(ReaderContent.Paragraph(styledText))
                            } else {
                                // 非文本节点
                                traverseHtmlElement(bookUri, node, accumulator, chapterRelativePath)
                            }
                        }

                        "image" -> {
                            logV {
                                "Encountered an image tag"
                            }
                            var src =
                                node.attr("xlink:href")
                                    .trim()

                            if (src.isEmpty()) {
                                src = node.attr("src").trim()
                            }

                            if (src.isEmpty()) {
                                src = node.attr(node.attr("href")).trim()
                            }
                            // ../Images/cover.jpg -> OEBPS/Images/cover.jpg
                            val imageRelativePath = resolveRelativePath(chapterRelativePath, src)
                            // 添加图片缓存
                            val imagePath = extractCoverToCache(
                                context = context,
                                bookUri,
                                imageRelativePath
                            )

                            if (imagePath.isNullOrEmpty()) {
                                logE {
                                    "Failed to cache the image, the cached image path is empty"
                                }
                                continue
                            }
                            logV {
                                "The cached image path is $imagePath"
                            }

                            accumulator.add(
                                ReaderContent.Image(
                                    path = imagePath
                                )
                            )
                        }

                        "img" -> {
                            logV {
                                "Encountered an img tag"
                            }

                            var src =
                                node.attr("src")
                                    .trim()

                            if (src.isEmpty()) {
                                src = node.attr("href").trim()
                            }

                            if (src.isEmpty()) {
                                src = node.attr(node.attr("xlink:href")).trim()
                            }

                            val imageRelativePath = resolveRelativePath(chapterRelativePath, src)
                            val imagePath = extractCoverToCache(
                                context = context,
                                bookUri,
                                imageRelativePath
                            )

                            if (imagePath.isNullOrEmpty()) {
                                logE {
                                    "Failed to cache the image, the cached image path is empty"
                                }
                                continue
                            }
                            logD {
                                "The cached image path is $imagePath"
                            }

                            accumulator.add(
                                ReaderContent.Image(
                                    path = imagePath
                                )
                            )
                        }

                        "figure", "div" -> {
                            // 遇到容器类标签, 继续递归
                            logV {
                                "Encountered a container tag: ${node.tagName()}"
                            }
                            traverseHtmlElement(bookUri, node, accumulator, chapterRelativePath)
                        }

                        "svg" -> {
                            logV {
                                "Encountered an svg tag"
                            }
                            traverseHtmlElement(bookUri, node, accumulator, chapterRelativePath)
                        }

                        else -> {
                            logV {
                                "Encountered an unknown tag: ${node.tagName()}"
                            }
                            traverseHtmlElement(bookUri, node, accumulator, chapterRelativePath)
                        }
                    }
                }
            }
        }
    }


    /**
     * 解析带样式的文本 (Bold, Italic)
     * 将 Jsoup Element 转为 Compose AnnotatedString
     * @param element 元素节点
     */
    private fun parseStyledText(element: Element): AnnotatedString {
        return buildAnnotatedString {
            // 递归遍历段落内部的子节点
            fun appendNode(node: Node) {
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


    /**
     * 解析相对路径引用
     * @param baseFile 当前文件的路径，例如：OEBPS/Text/chap1.html
     * @param relativeUrl 相对路径，例如：../Images/1.jpg
     */
    private fun resolveRelativePath(baseFile: String, relativeUrl: String): String {
        try {
            val baseDir = if (baseFile.contains("/")) baseFile.substringBeforeLast("/") else ""

            val fakeRoot = java.net.URI.create("file:///root/")
            val baseUri = fakeRoot.resolve("$baseDir/")
            // 利用 URI 解析相对路径
            val resolved = baseUri.resolve(relativeUrl)

            return resolved.path.removePrefix("/root/")
        } catch (_: Exception) {
            logE {
                "Failed to resolve the relative path: $relativeUrl from base file: $baseFile"
            }
            return relativeUrl // 失败降级
        }
    }
}