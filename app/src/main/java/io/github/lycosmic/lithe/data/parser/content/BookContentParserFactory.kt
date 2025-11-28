package io.github.lycosmic.lithe.data.parser.content

import io.github.lycosmic.lithe.data.model.FileFormat
import io.github.lycosmic.lithe.data.parser.content.epub.EpubContentParser
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookContentParserFactory @Inject constructor(
    private val epubContentParser: EpubContentParser
) {

    fun getParser(fileFormat: String): BookContentParser? {
        return when (fileFormat) {
            FileFormat.EPUB.value -> epubContentParser
            FileFormat.TXT.value -> null
            FileFormat.PDF.value -> null
            else -> {
                Timber.e("Unknown file format: %s", fileFormat)
                null
            }
        }
    }
}