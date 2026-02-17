package io.github.lycosmic.domain.model


enum class FileFormat(val value: String) {
    EPUB("epub"),
    PDF("pdf"),
    TXT("txt"),
    UNKNOWN("unknown");

    companion object {
        val EPUB_EXTENSION = ".${EPUB.value}"
        val PDF_EXTENSION = ".${PDF.value}"
        val TXT_EXTENSION = ".${TXT.value}"

        fun fromValue(value: String): FileFormat {
            return entries.firstOrNull { it.value == value } ?: UNKNOWN
        }
    }
}