package io.github.lycosmic.lithe.data.parser.content.epub

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.lycosmic.lithe.data.parser.content.ContentParserStrategy
import io.github.lycosmic.lithe.log.logD
import io.github.lycosmic.lithe.log.logE
import io.github.lycosmic.lithe.log.logI
import io.github.lycosmic.lithe.log.logV
import io.github.lycosmic.lithe.util.ZipUtils
import io.github.lycosmic.lithe.util.ZipUtils.extractCoverToCache
import io.github.lycosmic.model.BookChapter
import io.github.lycosmic.model.BookContentBlock
import io.github.lycosmic.model.EpubChapter
import io.github.lycosmic.model.StyleRange
import io.github.lycosmic.model.StyleType
import io.github.lycosmic.model.TxtChapter
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
) : ContentParserStrategy {
    /**
     * 使用 Jsoup 解析 Epub 书籍目录
     * @param uriString 书籍 URI
     */
    override suspend fun parseChapter(bookId: Long, uriString: String): List<BookChapter> {
        return emptyList()
    }



    /**
     * 使用 Jsoup 解析书籍章节内容 (HTML)
     * @param item 书籍章节
     */
    override suspend fun loadChapter(
        uriString: String,
        chapter: BookChapter
    ): List<BookContentBlock> = withContext(Dispatchers.IO) {

        // 章节 ZIP 路径，例如 OEBPS/Text/chap1.html
        val spineZipHref = when (chapter) {
            is EpubChapter -> chapter.href
            is TxtChapter -> {
                logE {
                    ""
                }
                return@withContext emptyList()
            }
        }


        val bookUri = uriString.toUri()
        val readerContentList = mutableListOf<BookContentBlock>()

        ZipUtils.findZipEntryAndAction(
            context,
            bookUri,
            spineZipHref
        ) { inputStream ->
            // 使用 Jsoup 解析章节 HTML
            val doc = Jsoup.parse(inputStream, Charsets.UTF_8.toString(), spineZipHref)
            val body = doc.body()

            // 递归遍历 HTML 节点
            val readerContents = mutableListOf<BookContentBlock>()
            // 传入累加器
            traverseHtmlElement(bookUri, body, readerContents, spineZipHref)
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
        accumulator: MutableList<BookContentBlock>,
        chapterRelativePath: String
    ): Unit = withContext(Dispatchers.Default) {

        for (node in element.childNodes()) {
            when (node) {
                is TextNode -> {
                    // 纯文本节点
                    val text = node.text().trim()
                    if (text.isNotEmpty()) {
                        // 段落
                        accumulator.add(BookContentBlock.Paragraph(text, emptyList()))
                    }
                }

                is Comment -> {
                    // 忽略注释
                }

                is Element -> {
                    when (node.tagName()) {
                        "h1", "h2", "h3", "h4", "h5", "h6" -> {

                            val title = node.text().trim()
                            if (title.isNotEmpty()) {
                                accumulator.add(
                                    BookContentBlock.Title(
                                        text = title,
                                        level = node.tagName().substring(1).toInt()
                                    )
                                )

                                accumulator.add(BookContentBlock.Divider)
                            } else {
                                traverseHtmlElement(bookUri, node, accumulator, chapterRelativePath)
                            }
                        }

                        "p" -> {
                            // 解析段落
                            val paragraph = parseStyledText(node)
                            if (paragraph.text.trim().isNotEmpty()) {
                                accumulator.add(
                                    paragraph
                                )
                            } else {
                                // 非文本节点
                                traverseHtmlElement(bookUri, node, accumulator, chapterRelativePath)
                            }
                        }

                        "image" -> {
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
                                BookContentBlock.Image(
                                    path = imagePath
                                )
                            )
                        }

                        "img" -> {
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
                                BookContentBlock.Image(
                                    path = imagePath
                                )
                            )
                        }

                        "figure", "div" -> {
                            // 遇到容器类标签, 继续递归
                            traverseHtmlElement(bookUri, node, accumulator, chapterRelativePath)
                        }

                        "svg" -> {
                            traverseHtmlElement(bookUri, node, accumulator, chapterRelativePath)
                        }

                        else -> {
                            logD {
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
     * 将 Jsoup Element 转为书籍段落 (data class)
     * @param element 元素节点
     */
    private fun parseStyledText(element: Element): BookContentBlock.Paragraph {
        val builder = StringBuilder()
        val styles = mutableListOf<StyleRange>()

        // 递归遍历段落内部的子节点
        fun appendNode(node: Node, styleType: StyleType? = null) {
            if (node is TextNode) {
                val text = node.text()
                if (text.isBlank()) {
                    return
                }
                builder.append(text)
                styleType?.let {
                    styles.add(StyleRange(builder.length, text.length, styleType))
                }
            } else if (node is Element) {
                when (node.tagName()) {
                    // 粗体
                    "b", "strong" -> {
                        node.childNodes().forEach { appendNode(it, StyleType.BOLD) }
                    }

                    // 斜体
                    "i", "em" -> {
                        node.childNodes().forEach { appendNode(it, StyleType.ITALIC) }

                    }

                    "br" -> builder.append("\n")
                    else -> {
                        node.childNodes().forEach {
                            appendNode(it, null)
                        }
                    }
                }
            }
        }

        element.childNodes().forEach { appendNode(it, null) }

        return BookContentBlock.Paragraph(
            text = builder.toString(),
            styles = styles
        )
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
        } catch (e: Exception) {
            logE(e = e) {
                "Failed to resolve the relative path: $relativeUrl from base file: $baseFile"
            }
            return relativeUrl // 失败降级
        }
    }

}