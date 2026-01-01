package io.github.lycosmic.lithe.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.lycosmic.lithe.data.repository.BookRepositoryImpl
import io.github.lycosmic.lithe.data.repository.CategoryRepositoryImpl
import io.github.lycosmic.lithe.data.repository.DirectoryRepositoryImpl
import io.github.lycosmic.lithe.domain.repository.BookRepository
import io.github.lycosmic.lithe.domain.repository.CategoryRepository
import io.github.lycosmic.lithe.domain.repository.DirectoryRepository
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
}