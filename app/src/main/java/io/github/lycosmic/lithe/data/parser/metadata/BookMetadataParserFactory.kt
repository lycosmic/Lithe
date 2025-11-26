package io.github.lycosmic.lithe.data.parser.metadata

import io.github.lycosmic.lithe.data.exception.UnsupportedFileTypeException
import io.github.lycosmic.lithe.data.model.FileType
import io.github.lycosmic.lithe.data.parser.metadata.epub.EpubMetadataParser
import io.github.lycosmic.lithe.data.parser.metadata.pdf.PdfMetadataParser
import io.github.lycosmic.lithe.data.parser.metadata.txt.TxtMetadataParser
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
    fun getParser(type: FileType): BookMetadataParser =
        when (type) {
            FileType.EPUB -> epubParser
            FileType.PDF -> pdfParser
            FileType.TXT -> txtParser
            else -> throw UnsupportedFileTypeException("Unsupported file type: $type")
        }
}