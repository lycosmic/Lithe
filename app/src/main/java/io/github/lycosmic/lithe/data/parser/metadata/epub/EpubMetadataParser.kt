package io.github.lycosmic.lithe.data.parser.metadata.epub

import android.content.Context
import android.net.Uri
import android.util.Xml
import io.github.lycosmic.lithe.data.parser.metadata.BookMetadataParser
import io.github.lycosmic.lithe.data.parser.metadata.epub.EpubMetadataParser.OpfResult.Companion.COVER
import io.github.lycosmic.lithe.data.parser.metadata.epub.EpubMetadataParser.OpfResult.Companion.CREATOR
import io.github.lycosmic.lithe.data.parser.metadata.epub.EpubMetadataParser.OpfResult.Companion.DESCRIPTION
import io.github.lycosmic.lithe.data.parser.metadata.epub.EpubMetadataParser.OpfResult.Companion.HREF
import io.github.lycosmic.lithe.data.parser.metadata.epub.EpubMetadataParser.OpfResult.Companion.ID
import io.github.lycosmic.lithe.data.parser.metadata.epub.EpubMetadataParser.OpfResult.Companion.IDENTIFIER
import io.github.lycosmic.lithe.data.parser.metadata.epub.EpubMetadataParser.OpfResult.Companion.ID_REF
import io.github.lycosmic.lithe.data.parser.metadata.epub.EpubMetadataParser.OpfResult.Companion.ITEM
import io.github.lycosmic.lithe.data.parser.metadata.epub.EpubMetadataParser.OpfResult.Companion.ITEM_REF
import io.github.lycosmic.lithe.data.parser.metadata.epub.EpubMetadataParser.OpfResult.Companion.LANGUAGE
import io.github.lycosmic.lithe.data.parser.metadata.epub.EpubMetadataParser.OpfResult.Companion.MEDIA_TYPE
import io.github.lycosmic.lithe.data.parser.metadata.epub.EpubMetadataParser.OpfResult.Companion.META
import io.github.lycosmic.lithe.data.parser.metadata.epub.EpubMetadataParser.OpfResult.Companion.NAME
import io.github.lycosmic.lithe.data.parser.metadata.epub.EpubMetadataParser.OpfResult.Companion.PACKAGE
import io.github.lycosmic.lithe.data.parser.metadata.epub.EpubMetadataParser.OpfResult.Companion.PUBLISHER
import io.github.lycosmic.lithe.data.parser.metadata.epub.EpubMetadataParser.OpfResult.Companion.SUBJECT
import io.github.lycosmic.lithe.data.parser.metadata.epub.EpubMetadataParser.OpfResult.Companion.TITLE
import io.github.lycosmic.lithe.data.parser.metadata.epub.EpubMetadataParser.OpfResult.Companion.UNIQUE_IDENTIFIER
import io.github.lycosmic.lithe.domain.model.BookSpineItem
import io.github.lycosmic.lithe.domain.model.EpubSpineItem
import io.github.lycosmic.lithe.domain.model.ParsedMetadata
import io.github.lycosmic.lithe.extension.logD
import io.github.lycosmic.lithe.extension.logE
import io.github.lycosmic.lithe.extension.logV
import io.github.lycosmic.lithe.extension.logW
import io.github.lycosmic.lithe.utils.ZipUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import java.io.BufferedInputStream
import java.io.InputStream
import java.net.URLDecoder
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class EpubMetadataParser @Inject constructor() : BookMetadataParser {

    /**
     * 提取元数据并保存封面到本地
     */
    override suspend fun parse(
        context: Context,
        uri: Uri
    ): ParsedMetadata {
        // OPF/OPS 文件相对路径
        val opfRelativePath = getOpfPathByContainer(context, uri) ?: getOpfPath(context, uri)
        ?: DEFAULT_OPF_RELATIVE_PATH

        // OPF/OPS 文件目录
        val opfParentPath = opfRelativePath.substring(0, opfRelativePath.lastIndexOf("/") + 1)

        // 解析 OPF 文件
        var parseResult: OpfResult? = null
        ZipUtils.findZipEntryAndAction(context, uri, opfRelativePath) { inputStream ->
            parseResult = parseOpfXml(inputStream)
        }

        logD {
            "OPF文件解析结果: $parseResult"
        }

        // 封面相对路径
        val coverRelativePath = parseResult?.coverHref

        // 解压封面图片, 并保存到本地
        var localCoverPath: String? = null
        if (coverRelativePath != null) {
            val decodedSrc = URLDecoder.decode(coverRelativePath, UTF_8)
            // 封面的完整 ZIP 相对路径(OEBPS/Images/157909.jpg)
            val zipRelativePath = opfParentPath + decodedSrc

            localCoverPath =
                ZipUtils.extractCoverToStorage(context, uri, zipRelativePath)
        }
        logD {
            "书籍封面已保存到本地路径: $localCoverPath"
        }

        // 根据 OPF 解析结果, 获取目录顺序
        val spineItems = mutableListOf<BookSpineItem>()
        val manifest = parseResult?.manifest
        val spine = parseResult?.spine
        if (manifest != null && spine != null) {
            // 获取章节文件相对路径
            val chapterRelativePaths = spine.mapNotNull { id ->
                val manifestItem = manifest[id]
                if (manifestItem != null) {
                    opfParentPath + manifestItem.href
                } else {
                    logD {
                        "书籍目录对应的章节文件不存在，目录ID: $id"
                    }
                    null
                }
            }

            // 解析章节文件获取标题
            val map = parseChapterTitlesBatch(
                context,
                uri,
                chapterRelativePaths
            )


            chapterRelativePaths.forEachIndexed { index, chapterRelativePath ->
                val title = map[chapterRelativePath] ?: "Unknown Title"
                val id = spine[index]
                spineItems.add(
                    EpubSpineItem(
                        id = id,
                        order = index,
                        contentHref = chapterRelativePath,
                        label = title
                    )
                )
            }
        }

        return ParsedMetadata(
            uniqueId = parseResult?.uniqueIdentifier,
            title = parseResult?.title,
            authors = parseResult?.authors,
            language = parseResult?.language,
            description = parseResult?.description,
            publisher = parseResult?.publisher,
            subjects = parseResult?.subjects,
            coverPath = localCoverPath,
            bookmarks = spineItems
        )
    }

    /**
     * 解析章节标题
     * @param targetPaths 章节文件的 ZIP 相对路径列表
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

                    logV {
                        "Chapter zip entry: $entryName"
                    }

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
            logE(e = e) {
                "Error parsing chapter titles, remaining paths: $remainingPaths"
            }
        }

        return@withContext results
    }

    private fun parseTitleFromStream(inputStream: InputStream): String {
        try {
            val parser = Xml.newPullParser()
            parser.setInput(inputStream, UTF_8)

            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (parser.name.equals(TITLE, ignoreCase = false)) {
                        return safeNextText(parser)
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            logW(e = e) {
                "Error parsing title, use default title Unknown"
            }
            return "Unknown"
        }
        return ""
    }

    /**
     * 解析 Container.xml 文件, 获取 Opf 文件相对路径
     */
    private suspend fun getOpfPathByContainer(context: Context, uri: Uri): String? {
        return ZipUtils.findZipEntryAndAction(
            context,
            uri,
            CONTAINER_RELATIVE_PATH
        ) { inputStream ->
            // 解析出 Opf 文件路径
            parseContainerXml(inputStream)
        }
    }


    /**
     * 解析出 OPF 文件相对路径
     */
    private suspend fun parseContainerXml(inputStream: InputStream): String? =
        withContext(Dispatchers.IO) {
            val parser = Xml.newPullParser()
            parser.setInput(inputStream, UTF_8)

            var eventType = parser.eventType

            while (eventType != XmlPullParser.END_DOCUMENT) {
                when (eventType) {
                    XmlPullParser.START_TAG -> {
                        val tagName = parser.name
                        if (tagName == ROOT_FILE) {
                            val rootFilePath = parser.getAttributeValue(null, FULL_PATH)
                            if (rootFilePath != null) {
                                return@withContext rootFilePath
                            }
                        }
                    }
                }
                eventType = parser.next()
            }

            return@withContext null
        }

    /**
     * 直接获取 Opf 文件相对路径
     */
    private suspend fun getOpfPath(context: Context, uri: Uri): String? =
        withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(uri).use { inputString ->
                ZipInputStream(inputString).use { zipInputStream ->
                    var entry: ZipEntry? = zipInputStream.nextEntry
                    while (entry != null) {
                        if (entry.name.endsWith(suffix = OPF_SUFFIX, ignoreCase = true)) {
                            return@withContext entry.name
                        }
                        entry = zipInputStream.nextEntry
                    }
                }
            }
            return@withContext null
        }

    /**
     * OPF 内容
     */
    private data class OpfResult(
        val uniqueIdentifier: String? = null,
        val title: String? = null,
        val authors: List<String> = emptyList(),
        val language: String? = null,
        val description: String? = null,
        val publisher: String? = null,
        val subjects: List<String> = emptyList(), // 标签
        val coverHref: String?, // 封面相对路径
        val manifest: Map<String, ManifestItem> = emptyMap(), // 资源清单: id -> item
        val spine: List<String> // 阅读顺序, 存放 id
    ) {
        data class ManifestItem(
            val href: String,
            val mediaType: String // 如 application/xhtml+xml, image/jpeg
        )

        companion object {
            const val PACKAGE = "package"
            const val UNIQUE_IDENTIFIER = "unique-identifier"
            const val TITLE = "title"
            const val CREATOR = "creator"
            const val IDENTIFIER = "identifier"
            const val LANGUAGE = "language"
            const val DESCRIPTION = "description"
            const val PUBLISHER = "publisher"
            const val SUBJECT = "subject"
            const val META = "meta"
            const val COVER = "cover"
            const val ITEM_REF = "itemref"
            const val NAME = "name"
            const val ITEM = "item"
            const val ID = "id"

            val COVER_IMAGE_LIST = listOf("coverimage", "cover_image", "cover-image")
            const val ID_REF = "idref"
            const val HREF = "href"
            const val MEDIA_TYPE = "media-type"
        }

    }

    /**
     * 解析 OPF 内容
     */
    private fun parseOpfXml(inputStream: InputStream): OpfResult {
        val parser = Xml.newPullParser()
        parser.setInput(inputStream, UTF_8)
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true)

        var uniqueIdRef: String? = null
        var uniqueIdentifier: String? = null
        var title: String? = null
        val authors = mutableListOf<String>()
        var language: String? = null
        var description: String? = null
        var publisher: String? = null
        val subjects = mutableListOf<String>()
        var coverId: String? = null
        var coverHref: String? = null
        val manifest = mutableMapOf<String, OpfResult.ManifestItem>()
        val spine = mutableListOf<String>()

        // 保存在Item中的封面id
        var itemCoverId: String? = null

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                val tagName = parser.name

                when (tagName) {
                    PACKAGE -> {
                        val uniqueId = parser.getAttributeValue(null, UNIQUE_IDENTIFIER)
                        if (uniqueId != null) {
                            uniqueIdRef = uniqueId
                        }
                    }

                    TITLE -> {
                        title = safeNextText(parser).trim()
                    }

                    CREATOR -> {
                        val author = safeNextText(parser).trim()
                        authors.add(author)
                    }

                    IDENTIFIER -> {
                        if (uniqueIdRef != null && uniqueIdRef == parser.getAttributeValue(
                                null,
                                ID
                            )
                        ) {
                            uniqueIdentifier = safeNextText(parser).trim()
                        }
                    }

                    LANGUAGE -> {
                        language = safeNextText(parser).trim()
                    }

                    DESCRIPTION -> {
                        description = safeNextText(parser).trim()
                    }

                    PUBLISHER -> {
                        publisher = safeNextText(parser).trim()
                    }

                    SUBJECT -> {
                        val subject = safeNextText(parser).trim()
                        subjects.add(subject)
                    }


                    META -> {
                        val attrName = parser.getAttributeValue(null, NAME)
                        if (attrName == COVER) {
                            coverId = parser.getAttributeValue(null, CONTENT)
                        }
                    }

                    ITEM -> {
                        val id = parser.getAttributeValue(null, ID)
                        if (OpfResult.COVER_IMAGE_LIST.contains(id)) {
                            itemCoverId = id
                        }

                        val href = parser.getAttributeValue(null, HREF)
                        val mediaType = parser.getAttributeValue(null, MEDIA_TYPE)

                        manifest[id] = OpfResult.ManifestItem(href, mediaType)
                    }

                    ITEM_REF -> {
                        val id = parser.getAttributeValue(null, ID_REF)
                        spine.add(id)
                    }
                }
            }
            eventType = parser.next()
        }

        // 获取封面路径
        if (coverId != null && manifest.containsKey(coverId)) {
            val item = manifest[coverId]
            coverHref = item?.href
            logD {
                "从元数据中获取封面路径: $coverHref"
            }
        } else if (itemCoverId != null && manifest.containsKey(itemCoverId)) {
            logD {
                "元数据中获取封面路径失败，尝试从Item中获取封面路径。"
            }
            // 尝试再次获取封面图片
            val item = manifest[itemCoverId]
            coverHref = item?.href
            logD {
                "从Item中获取封面路径: $coverHref"
            }
        }

        return OpfResult(
            uniqueIdentifier,
            title ?: "Unknown Title",
            authors,
            language,
            description,
            publisher,
            subjects,
            coverHref,
            manifest,
            spine
        )
    }

    /**
     * 安全读取 Text，防止 XML 格式不标准导致崩溃
     */
    private fun safeNextText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag() // 移至 END_TAG
        }
        return result
    }

    companion object {
        private const val CONTAINER_RELATIVE_PATH = "META-INF/container.xml"
        private const val DEFAULT_OPF_RELATIVE_PATH = "OEBPS/content.opf"
        private const val ROOT_FILE = "rootfile"
        private const val FULL_PATH = "full-path"
        private const val OPF_SUFFIX = ".opf"
        private const val UTF_8 = "UTF-8"
        private const val CONTENT = "content"
    }
}