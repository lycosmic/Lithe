package io.github.lycosmic.lithe.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.lycosmic.lithe.data.parser.content.BookContentParserImpl
import io.github.lycosmic.lithe.data.parser.metadata.BookMetadataParserImpl
import io.github.lycosmic.repository.BookContentParser
import io.github.lycosmic.repository.BookMetadataParser

@Module
@InstallIn(SingletonComponent::class)
abstract class ParserModule {
    /**
     * 绑定书籍元数据解析器
     */
    @Binds
    internal abstract fun bindBookParser(impl: BookMetadataParserImpl): BookMetadataParser

    /**
     * 绑定书籍内容解析器
     */
    @Binds
    internal abstract fun bindContentParser(impl: BookContentParserImpl): BookContentParser
}