package io.github.lycosmic.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.lycosmic.data.parser.content.BookContentParserImpl
import io.github.lycosmic.data.parser.metadata.BookMetadataParserImpl
import io.github.lycosmic.data.repository.GsonJsonParserImpl
import io.github.lycosmic.domain.repository.BookContentParser
import io.github.lycosmic.domain.repository.BookMetadataParser
import io.github.lycosmic.domain.repository.JsonParser
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ParserModule {
    /**
     * 绑定书籍元数据解析器
     */
    @Binds
    @Singleton
    abstract fun bindBookParser(impl: BookMetadataParserImpl): BookMetadataParser

    /**
     * 绑定书籍内容解析器
     */
    @Binds
    @Singleton
    abstract fun bindContentParser(impl: BookContentParserImpl): BookContentParser

    /**
     * 绑定JSON解析器
     */
    @Binds
    @Singleton
    abstract fun bindJsonParser(impl: GsonJsonParserImpl): JsonParser
}