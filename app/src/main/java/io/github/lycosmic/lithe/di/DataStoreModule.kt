package io.github.lycosmic.lithe.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// DataStore 文件名
private const val LITHE_SETTINGS_NAME = "lithe_settings"

@InstallIn(SingletonComponent::class)
@Module
class DataStoreModule {


    @Singleton
    @Provides
    fun provideDataStore(
        @ApplicationContext
        context: Context
    ): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = {
                // 创建 DataStore 文件
                context.preferencesDataStoreFile(LITHE_SETTINGS_NAME)
            }
        )
    }

}