package io.github.lycosmic.lithe.data.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
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
import io.github.lycosmic.lithe.data.local.entity.CategoryEntity
import io.github.lycosmic.lithe.extension.logI
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
        ).addCallback(
            object : RoomDatabase.Callback() {
                // 在数据库文件创建时执行调用
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)

                    logI {
                        "插入默认分类"
                    }

                    val currentTime = System.currentTimeMillis()

                    db.execSQL(
                        "INSERT INTO ${CategoryEntity.TABLE_NAME} (id, name, createdAt) VALUES (${CategoryEntity.DEFAULT_CATEGORY_ID}, \"\", $currentTime)"
                    )
                }
            }
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