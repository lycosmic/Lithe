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
            logI {
                "开始解析文件元数据"
            }

            val parsedBooks = selectedFiles.map { file ->
                async {
                    logD {
                        "当前正在解析文件: ${file.name}"
                    }
                    val parser = parserFactory.getParser(file.type.value)
                    if (parser == null) {
                        logW {
                            "找不到对应的解析器，忽略该文件: ${file.name}"
                        }
                        return@async null
                    }

                    val fileMetadata = parser.parse(application, file.uri)

                    logD {
                        "解析文件元数据成功，详情如下：${fileMetadata}"
                    }

                    ParsedBook(
                        file = file,
                        metadata = fileMetadata
                    )
                }
            }.awaitAll()

            logD {
                "解析文件元数据完成，共有 ${parsedBooks.size} 本书"
            }
            parsedBooks.filterNotNull()
        }

    /**
     * 导入书籍
     */
    suspend fun importBook(parsedBooks: List<ParsedBook>) = withContext(Dispatchers.IO) {
        logD { "开始导入用户选择的书籍，共 ${parsedBooks.size} 本书" }

        var successCount = 0L

        parsedBooks.forEach { parsedBook ->
            val metadata = parsedBook.metadata
            val bookFile = parsedBook.file
            val uniqueId = metadata.uniqueId ?: bookFile.uri.toString()

            // 书签排序
            val sortedBookmarks = metadata.bookmarks?.sortedBy { it.order }

            if (bookRepository.getBookByUniqueId(uniqueId) != null) {
                // 已有该书籍
                logW {
                    "书库中已有该书籍: ${metadata.title}，唯一标符为 $uniqueId，忽略该文件: ${bookFile.name}"
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
            successCount++
        }

        logD { "导入用户选择的书籍完成，成功导入 $successCount 本书" }

        return@withContext successCount
    }
}