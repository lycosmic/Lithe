package io.github.lycosmic.lithe.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.lycosmic.lithe.data.local.dao.BookDao
import io.github.lycosmic.lithe.data.local.dao.CategoryDao
import io.github.lycosmic.lithe.data.local.dao.ColorPresetDao
import io.github.lycosmic.lithe.data.local.dao.DirectoryDao
import io.github.lycosmic.lithe.data.local.entity.Book
import io.github.lycosmic.lithe.data.local.entity.BookCategoryCrossRef
import io.github.lycosmic.lithe.data.local.entity.CategoryEntity
import io.github.lycosmic.lithe.data.local.entity.ColorPresetEntity
import io.github.lycosmic.lithe.data.local.entity.ScannedDirectory

@Database(
    entities = [
        // 书籍和分类
        Book::class,
        CategoryEntity::class,
        BookCategoryCrossRef::class,
        // 已授权的目录
        ScannedDirectory::class,
        // 颜色预设
        ColorPresetEntity::class,
    ],
    version = 1
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