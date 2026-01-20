package io.github.lycosmic.data.parser.content.epub

import android.net.Uri
import android.util.Xml
import androidx.core.net.toUri
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
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EpubContentParser @Inject constructor(
    private val logger: DomainLogger,
    private val opfHandler: EpubOpfHandler,
    private val zipProcessor: ZipProcessor
) : ContentParserStrategy {
    /**
     * 解析 Epub 书籍目录
     * 策略：
     * 1. 解析 OPF 获取章节文件列表
     * 2. 解析 toc.ncx 获取目录映射
     * 3. 遍历列表，如果在 ncx 中找不到，则解析 HTML 文件头部
     * 4. 兜底使用 OPF Manifest ID 或文件名
     */
    override suspend fun parseChapter(bookId: Long, uriString: String): List<BookChapter> =
        withContext(Dispatchers.IO) {
            val uri = uriString.toUri()

            // 找到并解析 OPF 文件
            val opfRelativePath = opfHandler.findOpfPath(uri) // OPF 文件相对路径
                ?: throw IllegalArgumentException("OPF 文件不存在")
            val opfParentPath = opfRelativePath.substringBeforeLast("/", "") // OPF 文件父目录
            val parseResult = opfHandler.getOpfParseResult(uri, opfRelativePath)
                ?: throw IllegalArgumentException("OPF 文件解析失败")


            val (spine, manifest) = parseResult.manifestSpineInfo
            if (spine.isEmpty() || manifest.isEmpty()) {
                throw IllegalArgumentException("无法解析书籍章节结构")
            }

            // 1. 解析 toc.ncx 文件，获得相对路径和标题的映射
            val ncxPath = findNcxPath(manifest, opfParentPath)
            val ncxTitleMap = parseTocNcxToMap(uri, ncxPath, opfParentPath)

            val chapters = mutableListOf<BookChapter>()

            // 2. 遍历 Spine 生成章节列表
            spine.forEachIndexed { index, id ->
                val href = manifest[id] ?: return@forEachIndexed

                // 拼接完整相对路径
                val fullPath = if (opfParentPath.isNotEmpty()) {
                    "${opfParentPath}/${href}"
                } else {
                    href
                }

                // 先从 toc.ncx Map 中获取
                var title = ncxTitleMap[fullPath]

                // 再从 HTML 文件头部中获取 <title> 标签
                if (title == null) {
                    title = parseTitleFromHtmlFile(uri, fullPath)
                }

                // 使用文件名兜底
                if (title.isNullOrBlank()
                    && href.isNotBlank()
                    && href.contains("/")
                ) {
                    title = href.substringAfterLast("/") // 使用文件名
                }

                chapters.add(
                    EpubChapter(
                        bookId = bookId,
                        index = index,
                        title = title ?: "Chapter ${index + 1}",
                        href = fullPath
                    )
                )
            }

            return@withContext chapters
        }

    /**
     * 寻找 NCX 文件的路径
     */
    private fun findNcxPath(manifest: Map<String, String>, opfParentPath: String): String {
        // 尝试寻找 key 为 ncx 的项
        var href = manifest["ncx"]

        if (href == null) {
            // 尝试寻找 href 后缀为 .ncx 的项
            href = manifest.values.find {
                it.endsWith(".ncx")
            }
        }

        return if (href != null) {
            if (opfParentPath.isNotEmpty()) {
                "${opfParentPath}/${href}"
            } else {
                href
            }
        } else {
            // 猜测默认路径
            logger.w {
                "找不到 NCX 文件，使用默认路径"
            }
            val defaultNcxPath = if (opfParentPath.isNotEmpty()) {
                "$opfParentPath/$TOC_NCX_FILE_NAME"
            } else {
                TOC_NCX_FILE_NAME
            }
            defaultNcxPath
        }
    }

    /**
     * 解析 toc.ncx 文件，返回 Map<Content Src, NavLabel Text>
     */
    private suspend fun parseTocNcxToMap(
        uri: Uri,
        ncxPath: String,
        opfParentPath: String
    ): Map<String, String> {
        val titleMap = mutableMapOf<String, String>()

        zipProcessor.findZipEntryAndAction(uri, ncxPath) { inputStream ->
            try {
                val parser = Xml.newPullParser()
                parser.setInput(inputStream, UTF_8_INPUT_ENCODING)

                var eventType = parser.eventType
                var currentLabel: String? = null

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (parser.name.equals(TEXT_TAG, ignoreCase = true)) {
                            currentLabel = parser.safeNextText()
                        } else if (parser.name.equals(CONTENT_TAG, ignoreCase = true)) {
                            val src = parser.getAttributeValue(null, SRC_ATTRIBUTE_NAME)
                            if (src != null && currentLabel != null) {
                                // 完整相对路径
                                val fullSrc = "$opfParentPath/$src"
                                titleMap[fullSrc] = currentLabel
                                currentLabel = null // 重置，防止复用
                            }
                        }
                    }
                    eventType = parser.next()
                }
            } catch (e: Exception) {
                logger.w(throwable = e) { "解析 toc.ncx 失败: $ncxPath" }
            }
        }
        return titleMap
    }


    /**
     * 单独解析某个 HTML 文件的 Title 标签
     */
    private suspend fun parseTitleFromHtmlFile(uri: Uri, path: String): String? {
        var title: String? = null
        zipProcessor.findZipEntryAndAction(uri, path) { inputStream ->
            title = parseTitleFromStream(inputStream)
        }
        return title
    }

    /**
     * 从输入流中解析 HTML <title>
     */
    private fun parseTitleFromStream(inputStream: InputStream): String? {
        try {
            val parser = Xml.newPullParser()
            parser.setInput(inputStream, UTF_8_INPUT_ENCODING)

            var eventType = parser.eventType
            // 只需要扫描头部，不需要扫描整个文件
            // 设置一个简单的阈值防止无限读取
            var tagsChecked = 0
            val maxTagsToCheck = 50

            while (eventType != XmlPullParser.END_DOCUMENT && tagsChecked < maxTagsToCheck) {
                if (eventType == XmlPullParser.START_TAG) {
                    tagsChecked++
                    if (parser.name.equals(TITLE_TAG, ignoreCase = true)) {
                        return parser.safeNextText().takeIf { it.isNotBlank() }
                    }
                    // 如果已经到了 body，说明 title 没希望了，直接退出
                    if (parser.name.equals(BODY_TAG, ignoreCase = true)) {
                        break
                    }
                }
                eventType = parser.next()
            }
            return null
        } catch (_: Exception) {
            return null
        }
    }

    companion object {
        // --- 文件名称 ---
        private const val TOC_NCX_FILE_NAME = "toc.ncx"

        // --- 编码 ---
        private const val UTF_8_INPUT_ENCODING = "UTF-8"

        // --- HTML 标签 ---
        private const val TITLE_TAG = "title"

        private const val CONTENT_TAG = "content"

        private const val TEXT_TAG = "text"

        private const val BODY_TAG = "body"

        // --- 属性名 ---
        private const val SRC_ATTRIBUTE_NAME = "src"

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
            traverseHtmlElement(bookUri, body, readerContents, 0, spineZipHref)
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
        currentCharIndex: Int, // 当前字符位置
        chapterRelativePath: String
    ): Unit = withContext(Dispatchers.Default) {

        var currentCharIndex = currentCharIndex

        for (node in element.childNodes()) {
            when (node) {
                is TextNode -> {
                    // 纯文本节点
                    val text = node.text().trim()
                    if (text.isNotEmpty()) {
                        // 段落
                        accumulator.add(
                            BookContentBlock.Paragraph(
                                text,
                                emptyList(),
                                currentCharIndex
                            )
                        )
                        currentCharIndex += text.length
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
                                        level = node.tagName().substring(1).toInt(),
                                        startIndex = currentCharIndex
                                    )
                                )

                                currentCharIndex += title.length

                                accumulator.add(BookContentBlock.Divider(currentCharIndex))
                                currentCharIndex += 1
                            } else {
                                traverseHtmlElement(
                                    bookUri,
                                    node,
                                    accumulator,
                                    currentCharIndex,
                                    chapterRelativePath
                                )
                            }
                        }

                        "p" -> {
                            // 解析段落
                            val paragraph = parseStyledText(node, currentCharIndex)
                            if (paragraph.text.trim().isNotEmpty()) {
                                accumulator.add(
                                    paragraph
                                )
                                currentCharIndex += paragraph.text.length
                            } else {
                                // 非文本节点
                                traverseHtmlElement(
                                    bookUri,
                                    node,
                                    accumulator,
                                    currentCharIndex,
                                    chapterRelativePath
                                )
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
                                    path = imagePath,
                                    startIndex = currentCharIndex
                                )
                            )
                            currentCharIndex += 1
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
                                    path = imagePath,
                                    startIndex = currentCharIndex
                                )
                            )
                            currentCharIndex += 1
                        }

                        "figure", "div" -> {
                            // 遇到容器类标签, 继续递归
                            traverseHtmlElement(
                                bookUri,
                                node,
                                accumulator,
                                currentCharIndex,
                                chapterRelativePath
                            )
                        }

                        "svg" -> {
                            traverseHtmlElement(
                                bookUri,
                                node,
                                accumulator,
                                currentCharIndex,
                                chapterRelativePath
                            )
                        }

                        else -> {
                            logger.w {
                                "遇到了不支持的标签: ${node.tagName()}"
                            }
                            traverseHtmlElement(
                                bookUri,
                                node,
                                accumulator,
                                currentCharIndex,
                                chapterRelativePath
                            )
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
    private fun parseStyledText(
        element: Element,
        currentCharIndex: Int
    ): BookContentBlock.Paragraph {
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
            styles = styles,
            startIndex = currentCharIndex
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