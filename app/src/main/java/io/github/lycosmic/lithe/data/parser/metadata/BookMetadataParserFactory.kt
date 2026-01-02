package io.github.lycosmic.lithe.data.parser.metadata

import io.github.lycosmic.lithe.data.parser.metadata.epub.EpubMetadataParser
import io.github.lycosmic.lithe.data.parser.metadata.pdf.PdfMetadataParser
import io.github.lycosmic.lithe.data.parser.metadata.txt.TxtMetadataParser
import io.github.lycosmic.lithe.log.logE
import io.github.lycosmic.model.FileFormat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookMetadataParserFactory @Inject constructor(
    private val txtParser: TxtMetadataParser,
    private val pdfParser: PdfMetadataParser,
    private val epubParser: EpubMetadataParser,
) {
    /**
     * 根据文件类型，获取对应的元数据解析器
     */
    fun getMetadataParser(fileFormat: FileFormat): MetadataParserStrategy? =
        when (fileFormat) {
            FileFormat.EPUB -> epubParser
            FileFormat.PDF -> pdfParser
            FileFormat.TXT -> txtParser
            FileFormat.UNKNOWN -> {
                logE {
                    "未知的文件类型，无法获取对应的元数据解析器"
                }
                null
            }
        }
}