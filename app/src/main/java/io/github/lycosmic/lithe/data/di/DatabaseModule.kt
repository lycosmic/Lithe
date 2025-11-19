package io.github.lycosmic.lithe.data.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.lycosmic.lithe.data.local.AppDatabase
import io.github.lycosmic.lithe.data.local.BookDao
import io.github.lycosmic.lithe.data.local.DirectoryDao
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext
        context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = AppDatabase::class.java,
            name = AppDatabase.DATABASE_NAME
        ).build()
    }


    @Singleton
    @Provides
    fun provideBookDao(
        appDatabase: AppDatabase
    ): BookDao {
        return appDatabase.bookDao()
    }

    @Singleton
    @Provides
    fun provideDirectoryDao(
        appDatabase: AppDatabase
    ): DirectoryDao {
        return appDatabase.directoryDao()
    }
}