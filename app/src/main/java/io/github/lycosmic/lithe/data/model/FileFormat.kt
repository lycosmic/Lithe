package io.github.lycosmic.lithe.data.model


private const val EPUB_VALUE = "epub"
private const val PDF_VALUE = "pdf"
private const val TXT_VALUE = "txt"
private const val UNKNOWN_VALUE = "unknown"

enum class FileFormat(val value: String) {
    EPUB(EPUB_VALUE),
    PDF(PDF_VALUE),
    TXT(TXT_VALUE),
    UNKNOWN(UNKNOWN_VALUE);

    companion object {
        const val EPUB_EXTENSION = ".$EPUB_VALUE"
        const val PDF_EXTENSION = ".$PDF_VALUE"
        const val TXT_EXTENSION = ".$TXT_VALUE"
    }
}