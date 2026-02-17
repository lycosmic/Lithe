package io.github.lycosmic.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.lycosmic.data.counter.RealBookLengthCalculator
import io.github.lycosmic.domain.length.BookLengthCalculator

@Module
@InstallIn(SingletonComponent::class)
abstract class BookDataModule {

    @Binds
    abstract fun bindBookLengthCalculator(
        impl: RealBookLengthCalculator
    ): BookLengthCalculator
}