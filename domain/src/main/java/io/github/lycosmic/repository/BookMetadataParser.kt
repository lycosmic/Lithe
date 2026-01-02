package io.github.lycosmic.repository

import io.github.lycosmic.model.BookMetadata
import io.github.lycosmic.model.FileFormat

interface BookMetadataParser {
    /**
     * 从文件中解析书籍元数据，用于导入
     * @param uriString 文件路径
     * @param format 文件格式
     * @return 书籍元数据
     */
    suspend fun parse(uriString: String, format: FileFormat): Result<BookMetadata>
}