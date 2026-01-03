package io.github.lycosmic.data.parser.metadata.epub

import androidx.core.net.toUri
import io.github.lycosmic.data.parser.epub.helper.EpubOpfHandler
import io.github.lycosmic.data.parser.metadata.MetadataParserStrategy
import io.github.lycosmic.data.util.ZipProcessor
import io.github.lycosmic.domain.model.BookMetadata
import io.github.lycosmic.domain.util.DomainLogger
import java.net.URLDecoder
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class EpubMetadataParser @Inject constructor(
    private val logger: DomainLogger,
    private val zipProcessor: ZipProcessor,
    private val opfHandler: EpubOpfHandler
) : MetadataParserStrategy {

    /**
     * 提取元数据并保存封面到本地
     */
    override suspend fun parse(
        uriString: String
    ): BookMetadata {
        val uri = uriString.toUri()
        // OPF 文件相对路径
        val opfRelativePath =
            opfHandler.findOpfPath(uri) ?: throw IllegalArgumentException("OPF 文件不存在")

        // OPF 文件目录
        val opfParentPath = opfRelativePath.take(opfRelativePath.lastIndexOf("/") + 1)

        // 解析 OPF 文件
        val parseResult: EpubOpfHandler.OpfParseResult =
            opfHandler.getOpfParseResult(uri, opfParentPath)
                ?: throw IllegalArgumentException("OPF 文件解析失败")

        logger.d {
            "OPF文件解析结果: $parseResult"
        }

        // 封面相对路径
        val coverRelativePath = parseResult.coverHref

        // 解压封面图片, 并保存到本地
        var localCoverPath: String? = null
        if (coverRelativePath != null) {
            val decodedSrc = URLDecoder.decode(coverRelativePath, UTF_8_INPUT_ENCODING)
            // 封面的完整 ZIP 相对路径(OEBPS/Images/157909.jpg)
            val zipRelativePath = opfParentPath + decodedSrc

            localCoverPath =
                zipProcessor.extractCoverToStorage(uri, zipRelativePath)
        }
        logger.d {
            "书籍封面已保存到本地路径: $localCoverPath"
        }

        return BookMetadata(
            uniqueId = parseResult.uniqueIdentifier,
            title = parseResult.title,
            authors = parseResult.authors,
            language = parseResult.language,
            description = parseResult.description,
            publisher = parseResult.publisher,
            subjects = parseResult.subjects,
            coverPath = localCoverPath,
        )
    }


    companion object {
        private const val UTF_8_INPUT_ENCODING = "UTF-8"
    }
}