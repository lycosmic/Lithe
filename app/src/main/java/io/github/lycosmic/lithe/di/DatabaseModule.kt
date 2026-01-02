package io.github.lycosmic.lithe.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.lycosmic.lithe.data.local.AppDatabase
import io.github.lycosmic.lithe.data.local.dao.BookDao
import io.github.lycosmic.lithe.data.local.dao.CategoryDao
import io.github.lycosmic.lithe.data.local.dao.ColorPresetDao
import io.github.lycosmic.lithe.data.local.dao.DirectoryDao
import javax.inject.Singleton

/**
 * 提供数据库相关依赖
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * 提供 AppDatabase 的单例实例
     */
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

    @Singleton
    @Provides
    fun provideColorPresetDao(
        appDatabase: AppDatabase
    ): ColorPresetDao {
        return appDatabase.colorPresetDao()
    }

    @Singleton
    @Provides
    fun provideCategoryDao(
        appDatabase: AppDatabase
    ): CategoryDao {
        return appDatabase.categoryDao()
    }
}