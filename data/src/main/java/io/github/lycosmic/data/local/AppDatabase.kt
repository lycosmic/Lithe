package io.github.lycosmic.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.lycosmic.data.local.dao.BookDao
import io.github.lycosmic.data.local.dao.CategoryDao
import io.github.lycosmic.data.local.dao.ColorPresetDao
import io.github.lycosmic.data.local.dao.DirectoryDao
import io.github.lycosmic.data.local.entity.AuthorizedDirectory
import io.github.lycosmic.data.local.entity.BookCategoryCrossRef
import io.github.lycosmic.data.local.entity.BookEntity
import io.github.lycosmic.data.local.entity.CategoryEntity
import io.github.lycosmic.data.local.entity.ChapterEntity
import io.github.lycosmic.data.local.entity.ColorPresetEntity

@Database(
    entities = [
        // 书籍和分类
        BookEntity::class,
        CategoryEntity::class,
        BookCategoryCrossRef::class,
        // 书籍目录
        ChapterEntity::class,
        // 已授权的目录
        AuthorizedDirectory::class,
        // 颜色预设
        ColorPresetEntity::class,
    ],
    version = 1,
    exportSchema = true // 导出数据库 schema 文件
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao

    abstract fun directoryDao(): DirectoryDao

    abstract fun colorPresetDao(): ColorPresetDao

    abstract fun categoryDao(): CategoryDao

    companion object {
        const val DATABASE_NAME = "lithe_db"
    }
}