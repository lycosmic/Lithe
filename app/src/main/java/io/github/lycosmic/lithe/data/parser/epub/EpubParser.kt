package io.github.lycosmic.lithe.data.parser.epub

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Xml
import io.github.lycosmic.lithe.data.model.ParsedMetadata
import io.github.lycosmic.lithe.data.parser.BookParser
import org.xmlpull.v1.XmlPullParser
import java.io.File
import java.io.FileOutputStream
import java.net.URLDecoder
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class EpubParser @Inject constructor() : BookParser {

    /**
     * EPUB 解析, 提取元数据并保存封面到本地
     */
    override suspend fun parse(
        context: Context,
        uri: Uri
    ): ParsedMetadata {
        var title = ""
        var author = ""
        var description = ""
        var coverSrc: String? = null

        var opfParentPath = ""

        // 扫描 ZIP 文件找到 OPF 文件, 并解析元数据
        context.contentResolver.openInputStream(uri).use { inputStream ->
            ZipInputStream(inputStream).use { zipInputStream ->
                var entry: ZipEntry? = zipInputStream.nextEntry

                while (entry != null) {
                    if (entry.name.endsWith(suffix = ".opf", ignoreCase = true)) {
                        // 找到 OPF 文件, 记录父路径
                        opfParentPath = entry.name.substring(0, entry.name.lastIndexOf("/") + 1)

                        // 解析 XML
                        val parseResult = parseOpfXml(zipInputStream)
                        title = parseResult.title
                        author = parseResult.author
                        description = parseResult.description
                        coverSrc = parseResult.coverHref // 封面图片

                        break
                    }

                    entry = zipInputStream.nextEntry
                }
            }
        }


        // 解压封面图片, 并保存到本地
        var localCoverPath: String? = null
        if (coverSrc != null) {
            // 将 URL 编码的路径解码成 UTF-8
            val decodedSrc = URLDecoder.decode(coverSrc, "UTF-8")
            // 封面的完整 ZIP 路径 (OPF目录 + 相对路径)
            val fullZipPath = opfParentPath + decodedSrc

            localCoverPath = extractCoverToStorage(context, uri, fullZipPath, title)
        }

        return ParsedMetadata(title, author, description, localCoverPath)
    }

    /**
     * OPF 内容
     */
    private data class OpfResult(
        val title: String,
        val author: String,
        val description: String,
        val coverHref: String?
    )

    /**
     * 使用 XmlPullParser 解析 OPF 内容
     */
    private fun parseOpfXml(inputStream: java.io.InputStream): OpfResult {
        val parser = Xml.newPullParser()
        parser.setInput(inputStream, "UTF-8")

        var title = ""
        var author = ""
        var description = ""
        var coverId: String? = null
        var coverHref: String? = null

        // 第一张图片的 href
        var firstImageHref: String? = null

        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                val name = parser.name
                // dc:title -> title
                val localName = if (name.contains(":")) name.substringAfter(":") else name

                when (localName) {
                    "title" -> if (title.isEmpty()) title = safeNextText(parser)
                    "creator" -> if (author.isEmpty()) author = safeNextText(parser)
                    "description" -> if (description.isEmpty()) description = safeNextText(parser)

                    "meta" -> {
                        // 寻找 <meta name="cover" content="cover-id" />
                        val attrName = parser.getAttributeValue(null, "name")
                        if (attrName == "cover") {
                            coverId = parser.getAttributeValue(null, "content")
                        }
                    }

                    "item" -> {
                        // <item id="cover-id" href="cover.jpg" media-type="image/jpeg" />
                        val id = parser.getAttributeValue(null, "id")
                        val href = parser.getAttributeValue(null, "href")
                        val mediaType = parser.getAttributeValue(null, "media-type")

                        // 匹配 ID
                        if (coverId != null && id == coverId) {
                            coverHref = href
                        }

                        if (firstImageHref == null && mediaType?.startsWith("image/") == true) {
                            firstImageHref = href
                        }
                    }
                }
            }
            eventType = parser.next()
        }

        // 如果没找到明确的 coverId, 就使用第一张图片的 href
        return OpfResult(title, author, description, coverHref ?: firstImageHref)
    }

    /**
     * 辅助：安全读取 Text，防止 XML 格式不标准导致崩溃
     */
    private fun safeNextText(parser: XmlPullParser): String {
        return try {
            parser.nextText()
        } catch (_: Exception) {
            ""
        }
    }

    /**
     * 扫描 Zip, 将指定路径的图片保存应用私有目录
     */
    private fun extractCoverToStorage(
        context: Context,
        uri: Uri,
        targetZipPath: String,
        bookTitle: String
    ): String? {

        // 封面目录
        val coversDir = File(context.filesDir, "covers")
        if (!coversDir.exists()) coversDir.mkdirs()

        // 生成安全的文件名
        val safeName = bookTitle.replace("[^a-zA-Z0-9\\u4e00-\\u9fa5]".toRegex(), "_")
        val destFile = File(coversDir, "cover_${safeName}_${System.currentTimeMillis()}.jpg")

        context.contentResolver.openInputStream(uri)?.use { input ->
            ZipInputStream(input).use { zip ->
                var entry = zip.nextEntry
                while (entry != null) {
                    if (entry.name == targetZipPath) {
                        // 保存到本地
                        FileOutputStream(destFile).use { fos ->
                            // 压缩图片
                            val bitmap = BitmapFactory.decodeStream(zip)
                            bitmap?.compress(Bitmap.CompressFormat.JPEG, 80, fos)
                            bitmap?.recycle()
                        }
                        return destFile.absolutePath
                    }
                    entry = zip.nextEntry
                }
            }
        }

        return null
    }
}