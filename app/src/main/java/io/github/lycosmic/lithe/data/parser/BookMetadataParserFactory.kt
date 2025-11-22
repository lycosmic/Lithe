package io.github.lycosmic.lithe.data.parser

import android.content.Context
import android.net.Uri
import io.github.lycosmic.lithe.data.model.FileType
import io.github.lycosmic.lithe.data.model.ParsedMetadata
import io.github.lycosmic.lithe.data.parser.epub.EpubMetadataParser
import io.github.lycosmic.lithe.data.parser.pdf.PdfMetadataParser
import io.github.lycosmic.lithe.data.parser.txt.TxtMetadataParser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookMetadataParserFactory @Inject constructor(
    private val txtParser: TxtMetadataParser,
    private val pdfParser: PdfMetadataParser,
    private val epubParser: EpubMetadataParser,
) {

    fun getParser(type: FileType): BookMetadataParser =
        when (type) {
            FileType.EPUB -> epubParser
            FileType.PDF -> pdfParser
            FileType.TXT -> txtParser
            else -> object : BookMetadataParser {
                override suspend fun parse(
                    context: Context,
                    uri: Uri
                ): ParsedMetadata =
                    ParsedMetadata("未知文件", "未知作者")
            }
        }

}