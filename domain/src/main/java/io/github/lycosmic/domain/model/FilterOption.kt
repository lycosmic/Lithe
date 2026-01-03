package io.github.lycosmic.domain.model

data class FilterOption(
    val format: FileFormat,
    val displayName: String,
    val isSelected: Boolean = false,
) {
    companion object {
        val filterOptions = listOf(
            FilterOption(FileFormat.EPUB, FileFormat.EPUB_EXTENSION),
            FilterOption(FileFormat.PDF, FileFormat.PDF_EXTENSION),
            FilterOption(FileFormat.TXT, FileFormat.TXT_EXTENSION),
        )
    }
}