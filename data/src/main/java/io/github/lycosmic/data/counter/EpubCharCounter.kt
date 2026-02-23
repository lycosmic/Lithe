package io.github.lycosmic.data.counter

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.lycosmic.data.util.FileUtils
import io.github.lycosmic.domain.model.Book
import io.github.lycosmic.domain.model.BookChapter
import io.github.lycosmic.domain.util.DomainLogger
import org.jsoup.Jsoup
import java.io.InputStream
import java.util.zip.ZipInputStream
import javax.inject.Inject

class EpubCharCounter @Inject constructor(
    private val logger: DomainLogger,
    @param:ApplicationContext private val context: Context
) {

    fun count(book: Book, chapter: BookChapter): Long {
        logger.d { "开始统计EPUB章节字符数: ${chapter.title}" }
        
        return try {
            if (chapter !is BookChapter.EpubChapter) {
                logger.e { "无效的章节类型: ${chapter::class.simpleName}" }
                return 0L
            }

            // 直接使用输入流，避免文件复制
            FileUtils.openInputStream(context, book.fileUri)?.use { inputStream ->
                processEpubChapter(inputStream, chapter)
            } ?: run {
                logger.e { "无法打开EPUB文件输入流: ${book.fileUri}" }
                0L
            }
        } catch (e: Exception) {
            logger.e(throwable = e) {
                "统计EPUB章节 '${chapter.title}' 字符数失败: ${e.message}"
            }
            0L
        }
    }

    private fun processEpubChapter(
        inputStream: InputStream,
        chapter: BookChapter.EpubChapter
    ): Long {
        ZipInputStream(inputStream).use { zip ->
            val entryName = chapter.href.substringBefore("#")
            logger.d { "查找章节条目: $entryName" }

            var zipEntry = zip.nextEntry
            while (zipEntry != null) {
                if (zipEntry.name == entryName) {
                    logger.d { "找到章节条目，大小: ${zipEntry.size} bytes" }

                    val content = Jsoup.parse(zip, "UTF-8", "").text()
                    val charCount = content.length.toLong()
                    logger.d { "EPUB章节 '${chapter.title}' 字符统计完成: $charCount 字符" }
                    return charCount
                }
                zipEntry = zip.nextEntry
            }

            logger.e { "章节条目不存在: $entryName" }
            return 0L
        }
    }
}