package io.github.lycosmic.lithe.domain.usecase

import android.app.Application
import io.github.lycosmic.lithe.data.model.Book
import io.github.lycosmic.lithe.data.model.FileItem
import io.github.lycosmic.lithe.data.parser.metadata.BookMetadataParserFactory
import io.github.lycosmic.lithe.data.repository.BookRepository
import io.github.lycosmic.lithe.extension.logD
import io.github.lycosmic.lithe.extension.logI
import io.github.lycosmic.lithe.extension.logW
import io.github.lycosmic.lithe.presentation.browse.model.ParsedBook
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BookImportUseCase @Inject constructor(
    private val application: Application,
    private val parserFactory: BookMetadataParserFactory,
    private val bookRepository: BookRepository
) {
    /**
     * 解析文件元数据
     */
    suspend fun parseFileMetadata(selectedFiles: List<FileItem>): List<ParsedBook> =
        withContext(Dispatchers.IO) {
            logI { "Start parsing book metadata" }

            val parsedBooks = selectedFiles.map { file ->
                async {
                    logD { "Parsing metadata for file: ${file.name}" }
                    val parser = parserFactory.getParser(file.type.value)
                    if (parser == null) {
                        logW { "No parser found for file: ${file.name}" }
                        return@async null
                    }

                    val fileMetadata = parser.parse(application, file.uri)

                    logD { "Parsed metadata for file: ${file.name}, metadata: $fileMetadata" }

                    ParsedBook(
                        file = file,
                        metadata = fileMetadata
                    )
                }
            }.awaitAll()

            logI { "Finished parsing book metadata" }
            parsedBooks.filterNotNull()
        }

    /**
     * 导入书籍
     */
    suspend fun importBook(parsedBooks: List<ParsedBook>) = withContext(Dispatchers.IO) {
        logI { "Start importing the user's selected books" }

        logD { "User selected ${parsedBooks.size} books" }

        parsedBooks.forEach { parsedBook ->
            logD {
                "Books to be imported: ${parsedBook.file.name}, metadata: ${parsedBook.metadata}, file item: ${parsedBook.file}"
            }
            val metadata = parsedBook.metadata
            val bookFile = parsedBook.file
            val uniqueId = metadata.uniqueId ?: bookFile.uri.toString()

            // 书签排序
            val sortedBookmarks = metadata.bookmarks?.sortedBy { it.order }

            if (bookRepository.getBookByUniqueId(uniqueId) != null) {
                // 已有该书籍
                logW {
                    "The book is already in the database with a unique identifier of $uniqueId"
                }
                return@forEach
            }

            // 导入数据库
            val book = Book(
                title = metadata.title ?: "未知",
                author = metadata.authors?.firstOrNull() ?: "未知",
                description = metadata.description,
                coverPath = metadata.coverPath,
                fileSize = bookFile.size,
                fileUri = bookFile.uri.toString(),
                format = bookFile.type.name.lowercase(),
                importTime = System.currentTimeMillis(),
                uniqueId = uniqueId,
                language = metadata.language ?: "zh-CN",
                publisher = metadata.publisher ?: "未知",
                subjects = metadata.subjects ?: emptyList(),
                bookmarks = sortedBookmarks,
                lastReadPosition = null,
                lastReadTime = null
            )
            bookRepository.importBook(book)
        }

        logI { "Import books successfully" }
    }
}