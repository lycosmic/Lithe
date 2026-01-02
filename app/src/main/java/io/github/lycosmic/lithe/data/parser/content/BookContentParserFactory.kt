package io.github.lycosmic.lithe.data.parser.content

import io.github.lycosmic.lithe.data.parser.content.epub.EpubContentParser
import io.github.lycosmic.lithe.data.parser.content.txt.TxtContentParser
import io.github.lycosmic.lithe.log.logE
import io.github.lycosmic.model.FileFormat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookContentParserFactory @Inject constructor(
    private val epubContentParser: EpubContentParser,
    private val txtContentParser: TxtContentParser
) {

    fun getParser(fileFormat: FileFormat): ContentParserStrategy? {
        return when (fileFormat) {
            FileFormat.EPUB -> epubContentParser
            FileFormat.TXT -> txtContentParser
            FileFormat.PDF -> null
            else -> {
                logE {
                    "不支持解析内容的文件格式: $fileFormat"
                }
                null
            }
        }
    }
}