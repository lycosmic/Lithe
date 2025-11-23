package io.github.lycosmic.lithe.presentation.browse.model

import io.github.lycosmic.lithe.data.model.FileItem
import io.github.lycosmic.lithe.data.model.ParsedMetadata


/**
 * 待添加的书籍
 */
data class BookToAdd(
    val file: FileItem, // 文件
    val metadata: ParsedMetadata, // 书籍元数据
    val isSelected: Boolean = true  // 是否选中
)