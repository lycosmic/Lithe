package io.github.lycosmic.lithe.data.repository

import io.github.lycosmic.lithe.data.local.BookDao
import io.github.lycosmic.lithe.data.model.Book
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class BookRepository @Inject constructor(
    private val bookDao: BookDao
) {
    suspend fun importBook(book: Book) {
        bookDao.insertBook(book = book)
    }
}