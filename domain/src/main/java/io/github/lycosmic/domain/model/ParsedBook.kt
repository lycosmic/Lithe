package io.github.lycosmic.domain.model

/**
 * 解析后的书籍信息，包括文件信息和元数据
 */
data class ParsedBook(
    val file: FileItem, // 文件
    val metadata: BookMetadata, // 书籍元数据
    val isSelected: Boolean = true  // 是否选中
)