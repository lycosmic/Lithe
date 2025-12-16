package io.github.lycosmic.lithe.data.parser.metadata

import io.github.lycosmic.lithe.data.model.FileFormat
import io.github.lycosmic.lithe.data.parser.metadata.epub.EpubMetadataParser
import io.github.lycosmic.lithe.data.parser.metadata.pdf.PdfMetadataParser
import io.github.lycosmic.lithe.data.parser.metadata.txt.TxtMetadataParser
import io.github.lycosmic.lithe.extension.logE
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookMetadataParserFactory @Inject constructor(
    private val txtParser: TxtMetadataParser,
    private val pdfParser: PdfMetadataParser,
    private val epubParser: EpubMetadataParser,
) {
    /**
     * 根据文件类型, 获取对应的解析器
     */
    fun getParser(fileFormat: String): BookMetadataParser? =
        when (fileFormat) {
            FileFormat.EPUB.value -> epubParser
            FileFormat.PDF.value -> pdfParser
            FileFormat.TXT.value -> txtParser
            else -> {
                logE {
                    "不支持解析的文件格式: $fileFormat"
                }
                null
            }
        }
}