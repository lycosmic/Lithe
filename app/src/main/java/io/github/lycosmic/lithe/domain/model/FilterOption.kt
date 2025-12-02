package io.github.lycosmic.lithe.domain.model

import io.github.lycosmic.lithe.data.model.FileFormat

data class FilterOption(
    val format: FileFormat,
    val displayName: String,
    val isSelected: Boolean = false,
) {
    companion object {
        val defaultFilterOptions = listOf(
            FilterOption(FileFormat.EPUB, FileFormat.EPUB_EXTENSION),
            FilterOption(FileFormat.PDF, FileFormat.PDF_EXTENSION),
            FilterOption(FileFormat.TXT, FileFormat.TXT_EXTENSION),
        )
    }
}