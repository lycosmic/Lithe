package io.github.lycosmic.lithe.data.parser.content.epub

import android.content.Context
import android.net.Uri
import android.util.Log
import android.util.Xml
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import io.github.lycosmic.lithe.data.model.content.BookSpineItem
import io.github.lycosmic.lithe.data.model.content.ReaderContent
import io.github.lycosmic.lithe.data.parser.content.BookContentParser
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import org.xmlpull.v1.XmlPullParser
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EpubContentParser @Inject constructor() : BookContentParser {

    /**
     * 找出所有章节文件的路径和顺序
     */
    override suspend fun parseSpine(
        context: Context,
        uri: Uri
    ): List<BookSpineItem> {
        var opfPath = "" // OPF 文件路径
        val spineIds = mutableListOf<String>()        // id 列表
        val manifest = mutableMapOf<String, String>() // id -> href


        try {
            // 扫描 Zip 找到 OPF 文件
            context.contentResolver.openInputStream(uri)?.use { input ->
                ZipInputStream(input).use { zip ->
                    var entry = zip.nextEntry
                    while (entry != null) {
                        if (entry.name.endsWith(".opf", ignoreCase = true)) {
                            opfPath = entry.name
                            // 解析 OPF XML
                            parseOpfStructure(zip, manifest, spineIds)
                            break
                        }
                        entry = zip.nextEntry
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }

        // 组装 Spine 列表
        val opfDir = if (opfPath.contains("/")) opfPath.substringBeforeLast("/") + "/" else ""

        return spineIds.mapNotNull { id ->
            val href = manifest[id] ?: return@mapNotNull null
            // 拼接完整路径 (opf目录 + href)
            BookSpineItem(id = id, contentHref = opfDir + href, order = spineIds.indexOf(id))
        }
    }

    private fun parseOpfStructure(
        inputStream: java.io.InputStream,
        manifestMap: MutableMap<String, String>,
        spineList: MutableList<String>
    ) {
        val parser = Xml.newPullParser()
        parser.setInput(inputStream, "UTF-8")
        var eventType = parser.eventType

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                when (parser.name) {
                    "item" -> {
                        // <item id="item1" href="chapter1.html" ... />
                        val id = parser.getAttributeValue(null, "id")
                        val href = parser.getAttributeValue(null, "href")
                        if (id != null && href != null) {
                            manifestMap[id] = href
                        }
                    }

                    "itemref" -> {
                        // <itemref idref="item1" />
                        val idref = parser.getAttributeValue(null, "idref")
                        if (idref != null) {
                            spineList.add(idref)
                        }
                    }
                }
            }
            eventType = parser.next()
        }
    }

    /**
     * 利用 Jsoup 把 HTML 标签转成我们的 ReaderContent
     */
    override suspend fun parseContent(
        context: Context,
        bookUri: Uri,
        href: String
    ): List<ReaderContent> {
        val contentList = mutableListOf<ReaderContent>()

        try {
            context.contentResolver.openInputStream(bookUri)?.use { input ->
                ZipInputStream(input).use { zip ->
                    var entry = zip.nextEntry
                    while (entry != null) {
                        // 找到目标文件
                        if (entry.name == href) {
                            // 使用 Jsoup 解析 HTML 流
                            val doc = Jsoup.parse(zip, "UTF-8", href)
                            val body = doc.body()

                            // 递归遍历 HTML 节点
                            parseHtmlNode(context, bookUri, body, contentList, href)
                            return contentList
                        }
                        entry = zip.nextEntry
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("EpubParser", "Failed to parse chapter: $href", e)
            contentList.add(ReaderContent.Paragraph(AnnotatedString("加载章节失败")))
        }

        return contentList
    }

    /**
     * 将 HTML 节点转为 ReaderContent
     */
    private fun parseHtmlNode(
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
                            val cachedPath = extractImageToCache(context, bookUri, relativePath)
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
     * 提取图片到缓存
     */
    private fun extractImageToCache(context: Context, bookUri: Uri, zipPath: String): String? {
        val decodedPath = java.net.URLDecoder.decode(zipPath, "UTF-8")

        // 缓存文件名：使用路径的哈希，避免冲突
        val fileName = "img_${decodedPath.hashCode()}.jpg"
        val cacheDir = File(context.cacheDir, "reader_images")
        if (!cacheDir.exists()) cacheDir.mkdirs()

        val targetFile = File(cacheDir, fileName)

        // 如果缓存里已经有了，直接返回路径
        if (targetFile.exists() && targetFile.length() > 0) {
            return targetFile.absolutePath
        }

        // 没有则解压
        try {
            context.contentResolver.openInputStream(bookUri)?.use { input ->
                ZipInputStream(input).use { zip ->
                    var entry = zip.nextEntry
                    while (entry != null) {
                        if (entry.name == decodedPath) {
                            FileOutputStream(targetFile).use { output ->
                                zip.copyTo(output)
                            }
                            return targetFile.absolutePath
                        }
                        entry = zip.nextEntry
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
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