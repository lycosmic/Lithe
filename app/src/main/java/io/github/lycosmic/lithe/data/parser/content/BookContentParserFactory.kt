package io.github.lycosmic.lithe.data.parser.content

import io.github.lycosmic.lithe.data.model.FileFormat
import io.github.lycosmic.lithe.data.parser.content.epub.EpubContentParser
import io.github.lycosmic.lithe.data.parser.content.txt.TxtContentParser
import io.github.lycosmic.lithe.extension.logE
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookContentParserFactory @Inject constructor(
    private val epubContentParser: EpubContentParser,
    private val txtContentParser: TxtContentParser
) {

    fun getParser(fileFormat: String): BookContentParser? {
        return when (fileFormat) {
            FileFormat.EPUB.value -> epubContentParser
            FileFormat.TXT.value -> txtContentParser
            FileFormat.PDF.value -> null
            else -> {
                logE {
                    "不支持解析内容的文件格式: $fileFormat"
                }
                null
            }
        }
    }
}