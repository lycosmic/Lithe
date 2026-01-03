package io.github.lycosmic.data.parser.epub.helper

import android.content.Context
import android.net.Uri
import android.util.Xml
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.lycosmic.data.util.ZipProcessor
import io.github.lycosmic.data.util.safeNextText
import io.github.lycosmic.domain.util.DomainLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EpubOpfHandler @Inject constructor(
    @param:ApplicationContext
    private val context: Context,
    private val logger: DomainLogger,
    private val zipProcessor: ZipProcessor
) {
    /**
     * 获取 OPF 文件的相对路径
     * 策略：先从 Container.xml 文件中获取，若获取失败则全盘扫描。
     */
    suspend fun findOpfPath(uri: Uri): String? {
        // 尝试标准做法
        val pathFromContainer = parseContainerXml(uri)
        if (pathFromContainer != null) {
            return pathFromContainer
        } else {
            logger.w {
                "尝试从 Container.xml 文件中获取 OPF 文件失败，开始全盘扫描"
            }
        }

        // 降级策略
        val pathByScan = scanForOpf(uri)
        if (pathByScan != null) {
            return pathByScan
        } else {
            logger.w {
                "全盘扫描失败，请检查文件格式是否正确"
            }
            return null
        }
    }


    /**
     * 解析 Container.xml 文件，获取 OPF 文件相对路径
     */
    private suspend fun parseContainerXml(uri: Uri): String? =
        withContext(Dispatchers.IO) {
            // 找到 Container.xml 文件
            zipProcessor.findZipEntryAndAction(
                uri,
                CONTAINER_RELATIVE_PATH
            ) { inputStream ->
                // 解析 OPF 文件相对路径
                val parser = Xml.newPullParser()
                parser.setInput(inputStream, UTF_8_INPUT_ENCODING)

                var eventType = parser.eventType

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    when (eventType) {
                        XmlPullParser.START_TAG -> {
                            val tagName = parser.name
                            if (tagName == ROOT_FILE_TAG) {
                                val rootFilePath = parser.getAttributeValue(null, FULL_PATH_ATTR)
                                if (rootFilePath != null) {
                                    return@findZipEntryAndAction rootFilePath
                                }
                            }
                        }
                    }
                    eventType = parser.next()
                }
            }

            return@withContext null
        }

    /**
     * 直接扫描 Opf 文件相对路径
     */
    private suspend fun scanForOpf(uri: Uri): String? =
        withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(uri).use { inputString ->
                ZipInputStream(inputString).use { zipInputStream ->
                    var entry: ZipEntry? = zipInputStream.nextEntry
                    while (entry != null) {
                        if (entry.name.endsWith(suffix = OPF_FILE_SUFFIX, ignoreCase = true)) {
                            return@withContext entry.name
                        }
                        entry = zipInputStream.nextEntry
                    }
                }
            }
            return@withContext null
        }

    /**
     * 获取 OPF 文件解析结果
     */
    suspend fun getOpfParseResult(uri: Uri, opfRelativeZipPath: String): OpfParseResult? {
        return zipProcessor.findZipEntryAndAction(
            uri,
            opfRelativeZipPath
        ) { inputStream ->
            parseOpfXml(inputStream)
        }
    }

    /**
     * 解析 OPF 文件
     */
    private fun parseOpfXml(inputStream: InputStream): OpfParseResult {
        val parser = Xml.newPullParser()
        parser.setInput(inputStream, UTF_8_INPUT_ENCODING)
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
        val manifest = mutableMapOf<String, String>()
        val spine = mutableListOf<String>()

        // 保存在Item中的封面id
        var itemCoverId: String? = null

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                val tagName = parser.name

                when (tagName) {
                    PACKAGE_TAG -> {
                        val uniqueId =
                            parser.getAttributeValue(null, UNIQUE_IDENTIFIER_ATTR)
                        if (uniqueId != null) {
                            uniqueIdRef = uniqueId
                        }
                    }

                    TITLE_TAG -> {
                        title = parser.safeNextText().trim()
                    }

                    CREATOR_TAG -> {
                        val author = parser.safeNextText().trim()
                        authors.add(author)
                    }

                    IDENTIFIER_TAG -> {
                        if (uniqueIdRef != null && uniqueIdRef == parser.getAttributeValue(
                                null,
                                ID_ATTR
                            )
                        ) {
                            uniqueIdentifier = parser.safeNextText().trim()
                        }
                    }

                    LANGUAGE_TAG -> {
                        language = parser.safeNextText().trim()
                    }

                    DESCRIPTION_TAG -> {
                        description = parser.safeNextText().trim()
                    }

                    PUBLISHER_TAG -> {
                        publisher = parser.safeNextText().trim()
                    }

                    SUBJECT_TAG -> {
                        val subject = parser.safeNextText().trim()
                        subjects.add(subject)
                    }


                    META_TAG -> {
                        val attrValue = parser.getAttributeValue(null, NAME_ATTR)
                        if (attrValue == COVER_ATTR_VALUE) {
                            coverId = parser.getAttributeValue(null, CONTENT_ATTR)
                        }
                    }

                    ITEM_TAG -> {
                        val id = parser.getAttributeValue(null, ID_ATTR)
                        if (COVER_IMAGE_LIST.contains(id)) {
                            itemCoverId = id
                        }

                        val href = parser.getAttributeValue(null, HREF_ATTR)
                        manifest[id] = href
                    }

                    ITEM_REF_TAG -> { // spine
                        val idRef = parser.getAttributeValue(null, ID_REF_ATTR)
                        spine.add(idRef)
                    }
                }
            }
            eventType = parser.next()
        }

        // 获取封面路径
        if (coverId != null && manifest.containsKey(coverId)) {
            val coverHref = manifest[coverId]
            logger.d {
                "从元数据中获取封面路径: $coverHref"
            }
        } else if (itemCoverId != null && manifest.containsKey(itemCoverId)) {
            logger.d {
                "元数据中获取封面路径失败，尝试从Item中获取封面路径。"
            }
            // 尝试再次获取封面图片
            coverHref = manifest[itemCoverId]
            logger.d {
                "从Item中获取封面路径: $coverHref"
            }
        }

        return OpfParseResult(
            uniqueIdentifier,
            title,
            authors,
            language,
            description,
            publisher,
            subjects,
            coverHref,
            ManifestSpineInfo(
                spine,
                manifest
            )
        )
    }

    companion object {
        // --- 获取 OPF 文件相对路径的常量 ---
        private const val UTF_8_INPUT_ENCODING = "UTF-8"
        private const val ROOT_FILE_TAG = "rootfile"
        private const val FULL_PATH_ATTR = "full-path"
        private const val CONTAINER_RELATIVE_PATH = "META-INF/container.xml"
        private const val OPF_FILE_SUFFIX = ".opf"

        // --- 解析 OPF 文件的常量 ---
        // --- 标签名 ---
        private const val PACKAGE_TAG = "package"
        private const val TITLE_TAG = "title"
        private const val CREATOR_TAG = "creator"
        private const val IDENTIFIER_TAG = "identifier"
        private const val LANGUAGE_TAG = "language"
        private const val DESCRIPTION_TAG = "description"
        private const val PUBLISHER_TAG = "publisher"
        private const val SUBJECT_TAG = "subject"
        private const val META_TAG = "meta"
        private const val ITEM_REF_TAG = "itemref"
        private const val ITEM_TAG = "item"

        // --- 属性名 ---
        private const val UNIQUE_IDENTIFIER_ATTR = "unique-identifier"
        private const val NAME_ATTR = "name"
        private const val ID_REF_ATTR = "idref"
        private const val HREF_ATTR = "href"
        private const val CONTENT_ATTR = "content"
        private const val ID_ATTR = "id"

        // --- 属性值 ---
        private const val COVER_ATTR_VALUE = "cover"
        private val COVER_IMAGE_LIST = listOf("coverimage", "cover_image", "cover-image")
    }

    /**
     * 资源清单和阅读顺序信息
     */
    data class ManifestSpineInfo(
        val spine: List<String> = emptyList(), // 阅读顺序, 存放 id
        val manifest: Map<String, String> = emptyMap() // 存放资源清单: id -> href
    )


    /**
     * OPF 文件解析结果
     */
    data class OpfParseResult(
        val uniqueIdentifier: String? = null,
        val title: String? = null,
        val authors: List<String> = emptyList(),
        val language: String? = null,
        val description: String? = null,
        val publisher: String? = null,
        val subjects: List<String> = emptyList(), // 标签
        val coverHref: String?, // 封面相对路径
        val manifestSpineInfo: ManifestSpineInfo,
    )
}