package io.github.lycosmic.lithe.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.lycosmic.domain.util.DomainLogger
import io.github.lycosmic.lithe.log.TimberDomainLogger
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LogModule {

    @Binds
    @Singleton
    abstract fun bindDomainLogger(impl: TimberDomainLogger): DomainLogger
}