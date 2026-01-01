package io.github.lycosmic.lithe.domain.use_case.browse

import io.github.lycosmic.lithe.data.local.entity.BookCategoryCrossRef
import io.github.lycosmic.lithe.data.local.entity.BookEntity
import io.github.lycosmic.lithe.data.local.entity.CategoryEntity
import io.github.lycosmic.lithe.domain.repository.BookRepository
import io.github.lycosmic.lithe.extension.logD
import io.github.lycosmic.lithe.extension.logW
import io.github.lycosmic.lithe.presentation.browse.model.ParsedBook
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * 导入书籍
 */
class ImportBookUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(parsedBooks: List<ParsedBook>) = withContext(Dispatchers.IO) {
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
            val bookEntity = BookEntity(
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
            val bookId = bookRepository.importBook(bookEntity)
            // 添加到默认分类中
            val crossRef = BookCategoryCrossRef(
                bookId = bookId,
                categoryId = CategoryEntity.DEFAULT_CATEGORY_ID
            )
            bookRepository.insertBookCategoryCrossRefs(listOf(crossRef))

            successCount++
        }

        logD { "导入用户选择的书籍完成，成功导入 $successCount 本书" }

        return@withContext successCount
    }
}