package io.github.lycosmic.data.parser.content.epub

import android.content.Context
import android.net.Uri
import android.util.Xml
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.lycosmic.data.parser.content.ContentParserStrategy
import io.github.lycosmic.data.parser.epub.helper.EpubOpfHandler
import io.github.lycosmic.data.util.ZipProcessor
import io.github.lycosmic.data.util.safeNextText
import io.github.lycosmic.domain.model.BookChapter
import io.github.lycosmic.domain.model.BookContentBlock
import io.github.lycosmic.domain.model.EpubChapter
import io.github.lycosmic.domain.model.StyleRange
import io.github.lycosmic.domain.model.StyleType
import io.github.lycosmic.domain.model.TxtChapter
import io.github.lycosmic.domain.util.DomainLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Comment
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode
import org.xmlpull.v1.XmlPullParser
import java.io.BufferedInputStream
import java.io.InputStream
import java.util.zip.ZipInputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EpubContentParser @Inject constructor(
    @param:ApplicationContext
    private val context: Context,
    private val logger: DomainLogger,
    private val opfHandler: EpubOpfHandler,
    private val zipProcessor: ZipProcessor
) : ContentParserStrategy {
    /**
     * 使用 Jsoup 解析 Epub 书籍目录
     * @param uriString 书籍 URI
     */
    override suspend fun parseChapter(bookId: Long, uriString: String): List<BookChapter> {
        val uri = uriString.toUri()
        // OPF 文件相对路径
        val opfRelativePath =
            opfHandler.findOpfPath(uri) ?: throw IllegalArgumentException("OPF 文件不存在")

        // OPF 文件目录
        val opfParentPath = opfRelativePath.take(opfRelativePath.lastIndexOf("/") + 1)

        // 解析 OPF 文件
        val parseResult: EpubOpfHandler.OpfParseResult =
            opfHandler.getOpfParseResult(uri, opfRelativePath)
                ?: throw IllegalArgumentException("OPF 文件解析失败")

        // 根据 OPF 解析结果, 获取目录顺序
        val chapters = mutableListOf<BookChapter>()
        parseResult.manifestSpineInfo.let { (spine, manifest) ->
            if (spine.isNotEmpty() && manifest.isNotEmpty()) {
                // 获取章节文件的相对路径
                val chapterRelativePaths = spine.map { id ->
                    val href = manifest[id]
                    if (href != null) {
                        opfParentPath + href
                    } else {
                        throw IllegalArgumentException("书籍目录对应的章节文件不存在，目录ID: $id")
                    }
                }
                // 解析章节文件获取章节标题
                val map = parseChapterTitlesBatch(
                    context,
                    uri,
                    chapterRelativePaths
                )

                chapterRelativePaths.forEachIndexed { index, chapterRelativePath ->
                    val title = map[chapterRelativePath]
                        ?: throw IllegalArgumentException("无法获取章节标题")

                    chapters.add(
                        EpubChapter(
                            bookId = bookId,
                            index = index,
                            title = title,
                            href = chapterRelativePath
                        )
                    )
                }
            } else {
                throw IllegalArgumentException("无法解析书籍章节")
            }
        }

        return chapters
    }

    /**
     * 解析章节标题
     * @param targetPaths 章节文件的 ZIP 相对路径列表
     * @return 章节路径与标题的映射
     */
    suspend fun parseChapterTitlesBatch(
        context: Context,
        uri: Uri,
        targetPaths: List<String>
    ): Map<String, String> = withContext(Dispatchers.IO) {
        // 待处理的章节文件 ZIP 相对路径列表
        val remainingPaths = targetPaths.toMutableSet()
        val results = mutableMapOf<String, String>()

        if (remainingPaths.isEmpty()) return@withContext results

        try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri)

            inputStream?.use { stream ->
                val bufferedStream = BufferedInputStream(stream)
                val zipInputStream = ZipInputStream(bufferedStream)

                var zipEntry = zipInputStream.nextEntry

                while (zipEntry != null && remainingPaths.isNotEmpty()) {
                    val entryName = zipEntry.name

                    if (remainingPaths.contains(entryName)) {
                        val title = parseTitleFromStream(zipInputStream)

                        results[entryName] = title
                        remainingPaths.remove(entryName)
                    }

                    zipInputStream.closeEntry()
                    zipEntry = zipInputStream.nextEntry
                }
            }
        } catch (e: Exception) {
            logger.e(throwable = e) {
                "解析章节标题时出错，剩下待处理章节文件路径: $remainingPaths"
            }
            return@withContext results
        }

        return@withContext results
    }

    /**
     * 从输入流中解析章节标题
     * @param inputStream 输入流
     * @return 章节标题
     */
    private fun parseTitleFromStream(inputStream: InputStream): String {
        try {
            val parser = Xml.newPullParser()
            parser.setInput(inputStream, UTF_8_INPUT_ENCODING)

            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.name.equals(TITLE_TAG, ignoreCase = false)) {
                        return parser.safeNextText()
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            logger.w(throwable = e) {
                "在解析章节标题时出错"
            }
            throw e
        }

        throw IllegalArgumentException("无法解析章节标题")
    }

    companion object {
        // --- 编码 ---
        private const val UTF_8_INPUT_ENCODING = "UTF-8"

        // --- HTML 标签 ---
        private const val TITLE_TAG = "title"
    }

    /**
     * 使用 Jsoup 解析书籍章节内容 (HTML)
     * @param chapter 书籍章节
     */
    override suspend fun loadChapter(
        uriString: String,
        chapter: BookChapter
    ): List<BookContentBlock> = withContext(Dispatchers.IO) {
        // 章节 ZIP 路径，例如 OEBPS/Text/chap1.html
        val spineZipHref = when (chapter) {
            is EpubChapter -> chapter.href
            is TxtChapter -> {
                logger.e {
                    "要解析的章节不是 TXT 章节"
                }
                return@withContext emptyList()
            }
        }


        val bookUri = uriString.toUri()
        val readerContentList = mutableListOf<BookContentBlock>()

        zipProcessor.findZipEntryAndAction(
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
            logger.d {
                "The parsed reading content is $readerContents"
            }
            readerContentList.addAll(readerContents)
        }

        logger.i {
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
                            val imagePath = zipProcessor.extractCoverToCache(
                                bookUri,
                                imageRelativePath
                            )

                            if (imagePath.isNullOrEmpty()) {
                                logger.e {
                                    "Failed to cache the image, the cached image path is empty"
                                }
                                continue
                            }
                            logger.d {
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
                            val imagePath = zipProcessor.extractCoverToCache(
                                bookUri,
                                imageRelativePath
                            )

                            if (imagePath.isNullOrEmpty()) {
                                logger.e {
                                    "Failed to cache the image, the cached image path is empty"
                                }
                                continue
                            }
                            logger.d {
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
                            logger.d {
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
            logger.e(throwable = e) {
                "Failed to resolve the relative path: $relativeUrl from base file: $baseFile"
            }
            return relativeUrl // 失败降级
        }
    }

}