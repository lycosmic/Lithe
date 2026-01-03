package io.github.lycosmic.data.parser.metadata.epub

import android.content.Context
import android.net.Uri
import android.util.Xml
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.lycosmic.data.parser.metadata.MetadataParserStrategy
import io.github.lycosmic.data.util.ZipProcessor
import io.github.lycosmic.domain.model.BookChapter
import io.github.lycosmic.domain.model.BookMetadata
import io.github.lycosmic.domain.model.EpubChapter
import io.github.lycosmic.domain.model.ManifestItem
import io.github.lycosmic.domain.model.OpfParseResult
import io.github.lycosmic.domain.model.OpfParseResult.Companion.COVER
import io.github.lycosmic.domain.model.OpfParseResult.Companion.CREATOR
import io.github.lycosmic.domain.model.OpfParseResult.Companion.DESCRIPTION
import io.github.lycosmic.domain.model.OpfParseResult.Companion.HREF
import io.github.lycosmic.domain.model.OpfParseResult.Companion.ID
import io.github.lycosmic.domain.model.OpfParseResult.Companion.IDENTIFIER
import io.github.lycosmic.domain.model.OpfParseResult.Companion.ID_REF
import io.github.lycosmic.domain.model.OpfParseResult.Companion.ITEM
import io.github.lycosmic.domain.model.OpfParseResult.Companion.ITEM_REF
import io.github.lycosmic.domain.model.OpfParseResult.Companion.LANGUAGE
import io.github.lycosmic.domain.model.OpfParseResult.Companion.MEDIA_TYPE
import io.github.lycosmic.domain.model.OpfParseResult.Companion.META
import io.github.lycosmic.domain.model.OpfParseResult.Companion.NAME
import io.github.lycosmic.domain.model.OpfParseResult.Companion.PACKAGE
import io.github.lycosmic.domain.model.OpfParseResult.Companion.PUBLISHER
import io.github.lycosmic.domain.model.OpfParseResult.Companion.SUBJECT
import io.github.lycosmic.domain.model.OpfParseResult.Companion.TITLE
import io.github.lycosmic.domain.model.OpfParseResult.Companion.UNIQUE_IDENTIFIER
import io.github.lycosmic.domain.util.DomainLogger
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
class EpubMetadataParser @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val logger: DomainLogger,
    private val zipProcessor: ZipProcessor
) : MetadataParserStrategy {

    /**
     * 提取元数据并保存封面到本地
     */
    override suspend fun parse(
        uriString: String
    ): BookMetadata {
        val uri = uriString.toUri()

        // OPF/OPS 文件相对路径
        val opfRelativePath = getOpfPathByContainer(uri) ?: getOpfPath(context, uri)
        ?: DEFAULT_OPF_RELATIVE_PATH

        // OPF/OPS 文件目录
        val opfParentPath = opfRelativePath.take(opfRelativePath.lastIndexOf("/") + 1)

        // 解析 OPF 文件
        var parseResult: OpfParseResult? = null
        zipProcessor.findZipEntryAndAction(uri, opfRelativePath) { inputStream ->
            parseResult = parseOpfXml(inputStream)
        }

        logger.d {
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
                zipProcessor.extractCoverToStorage(uri, zipRelativePath)
        }
        logger.d {
            "书籍封面已保存到本地路径: $localCoverPath"
        }

        // 根据 OPF 解析结果, 获取目录顺序
        val spineItems = mutableListOf<BookChapter>()
        val manifest = parseResult?.manifest
        val spine = parseResult?.spine
        if (manifest != null && spine != null) {
            // 获取章节文件相对路径
            val chapterRelativePaths = spine.mapNotNull { id ->
                val manifestItem = manifest[id]
                if (manifestItem != null) {
                    opfParentPath + manifestItem.href
                } else {
                    logger.d {
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
                    EpubChapter(
                        bookId = 1000, // TODO: 获取书籍ID
                        index = index,
                        title = title,
                        href = chapterRelativePath
                    )
                )
            }
        }

        return BookMetadata(
            uniqueId = parseResult?.uniqueIdentifier,
            title = parseResult?.title,
            authors = parseResult?.authors,
            language = parseResult?.language,
            description = parseResult?.description,
            publisher = parseResult?.publisher,
            subjects = parseResult?.subjects,
            coverPath = localCoverPath,
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
            logger.w(throwable = e) {
                "Error parsing title, use default title Unknown"
            }
            return "Unknown"
        }
        return ""
    }

    /**
     * 解析 Container.xml 文件, 获取 Opf 文件相对路径
     */
    private suspend fun getOpfPathByContainer(uri: Uri): String? {
        return zipProcessor.findZipEntryAndAction(
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
     * 解析 OPF 内容
     */
    private fun parseOpfXml(inputStream: InputStream): OpfParseResult {
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
        val manifest = mutableMapOf<String, ManifestItem>()
        val spine = mutableListOf<String>()

        // 保存在Item中的封面id
        var itemCoverId: String? = null

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                val tagName = parser.name

                when (tagName) {
                    PACKAGE -> {
                        val uniqueId =
                            parser.getAttributeValue(null, UNIQUE_IDENTIFIER)
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
                        if (OpfParseResult.COVER_IMAGE_LIST.contains(id)) {
                            itemCoverId = id
                        }

                        val href = parser.getAttributeValue(null, HREF)
                        val mediaType = parser.getAttributeValue(null, MEDIA_TYPE)

                        manifest[id] = ManifestItem(href, mediaType)
                    }

                    ITEM_REF -> { // spine
                        val idRef = parser.getAttributeValue(null, ID_REF)
                        spine.add(idRef)
                    }
                }
            }
            eventType = parser.next()
        }

        // 获取封面路径
        if (coverId != null && manifest.containsKey(coverId)) {
            val item = manifest[coverId]
            coverHref = item?.href
            logger.d {
                "从元数据中获取封面路径: $coverHref"
            }
        } else if (itemCoverId != null && manifest.containsKey(itemCoverId)) {
            logger.d {
                "元数据中获取封面路径失败，尝试从Item中获取封面路径。"
            }
            // 尝试再次获取封面图片
            val item = manifest[itemCoverId]
            coverHref = item?.href
            logger.d {
                "从Item中获取封面路径: $coverHref"
            }
        }

        return OpfParseResult(
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