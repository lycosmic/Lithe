package io.github.lycosmic.lithe.data.parser.metadata

import android.content.Context
import android.net.Uri
import io.github.lycosmic.lithe.domain.model.ParsedMetadata

interface BookMetadataParser {
    /**
     * 从文件中解析元数据
     */
    suspend fun parse(context: Context, uri: Uri): ParsedMetadata
}