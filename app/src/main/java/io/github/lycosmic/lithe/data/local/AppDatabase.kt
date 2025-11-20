package io.github.lycosmic.lithe.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.lycosmic.lithe.data.model.Book
import io.github.lycosmic.lithe.data.model.ScannedDirectory

@Database(entities = [Book::class, ScannedDirectory::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao

    abstract fun directoryDao(): DirectoryDao

    companion object {
        const val DATABASE_NAME = "lithe_db"
    }
}