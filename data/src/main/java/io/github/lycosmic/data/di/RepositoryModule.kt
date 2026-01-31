package io.github.lycosmic.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.lycosmic.data.repository.BookRepositoryImpl
import io.github.lycosmic.data.repository.CategoryRepositoryImpl
import io.github.lycosmic.data.repository.ChapterRepositoryImpl
import io.github.lycosmic.data.repository.DirectoryRepositoryImpl
import io.github.lycosmic.data.repository.FontFamilyRepositoryImpl
import io.github.lycosmic.data.repository.ProgressRepositoryImpl
import io.github.lycosmic.domain.repository.BookRepository
import io.github.lycosmic.domain.repository.CategoryRepository
import io.github.lycosmic.domain.repository.ChapterRepository
import io.github.lycosmic.domain.repository.DirectoryRepository
import io.github.lycosmic.domain.repository.FontFamilyRepository
import io.github.lycosmic.domain.repository.ProgressRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindBookRepository(
        bookRepositoryImpl: BookRepositoryImpl
    ): BookRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl
    ): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindDirectoryRepository(
        directoryRepositoryImpl: DirectoryRepositoryImpl
    ): DirectoryRepository

    @Binds
    @Singleton
    abstract fun bindChapterRepository(
        chapterRepositoryImpl: ChapterRepositoryImpl
    ): ChapterRepository

    @Binds
    @Singleton
    abstract fun bindProgressRepository(
        progressRepositoryImpl: ProgressRepositoryImpl
    ): ProgressRepository

    @Binds
    @Singleton
    abstract fun bindFontRepository(impl: FontFamilyRepositoryImpl): FontFamilyRepository
}