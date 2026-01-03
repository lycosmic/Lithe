package io.github.lycosmic.data.parser.metadata

import io.github.lycosmic.domain.model.BookMetadata

/**
 * 元数据解析策略接口
 */
interface MetadataParserStrategy {
    suspend fun parse(uriString: String): BookMetadata
}