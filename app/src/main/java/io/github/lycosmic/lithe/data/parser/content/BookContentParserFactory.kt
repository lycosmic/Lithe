package io.github.lycosmic.lithe.data.parser.content

import io.github.lycosmic.lithe.data.parser.content.epub.EpubContentParser
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookContentParserFactory @Inject constructor(
    private val epubContentParser: EpubContentParser
) {

    fun getParser(format: String): BookContentParser? {
        return when (format) {
            "epub" -> epubContentParser
            "pdf" -> null
            else -> null
        }
    }
}