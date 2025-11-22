package io.github.lycosmic.lithe.data.parser

import android.content.Context
import android.net.Uri
import io.github.lycosmic.lithe.data.model.FileType
import io.github.lycosmic.lithe.data.model.ParsedMetadata
import io.github.lycosmic.lithe.data.parser.epub.EpubParser
import io.github.lycosmic.lithe.data.parser.pdf.PdfParser
import io.github.lycosmic.lithe.data.parser.txt.TxtParser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookParserFactory @Inject constructor(
    private val txtParser: TxtParser,
    private val pdfParser: PdfParser,
    private val epubParser: EpubParser,
) {

    fun getParser(type: FileType): BookParser =
        when (type) {
            FileType.EPUB -> epubParser
            FileType.PDF -> pdfParser
            FileType.TXT -> txtParser
            else -> object : BookParser {
                override suspend fun parse(
                    context: Context,
                    uri: Uri
                ): ParsedMetadata =
                    ParsedMetadata("未知文件", "未知作者")
            }
        }

}