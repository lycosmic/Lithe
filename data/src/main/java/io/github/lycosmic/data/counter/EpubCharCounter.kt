package io.github.lycosmic.data.counter

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.lycosmic.data.util.FileUtils
import io.github.lycosmic.domain.model.Book
import io.github.lycosmic.domain.model.BookChapter
import io.github.lycosmic.domain.util.DomainLogger
import org.jsoup.Jsoup
import java.util.zip.ZipFile
import javax.inject.Inject

class EpubCharCounter @Inject constructor(
    private val logger: DomainLogger,
    @param:ApplicationContext private val context: Context
) {

    fun count(book: Book, chapter: BookChapter): Long {
        val file = FileUtils.getFileFromUri(context, book.fileUri)
        if (file == null || !file.exists()) {
            logger.e { "File not found: ${book.fileUri}" }
            return 0L
        }

        if (chapter !is BookChapter.EpubChapter) {
            logger.e { "Invalid chapter type: ${chapter::class.simpleName}" }
            return 0L
        }

        return try {
            ZipFile(file).use { zip ->
                val entryName = chapter.href.substringBefore("#")
                val entry = zip.getEntry(entryName) ?: return 0L

                zip.getInputStream(entry).use { stream ->
                    Jsoup.parse(stream, "UTF-8", "").text().length.toLong()
                }
            }
        } catch (e: Exception) {
            logger.e(throwable = e) {
                "Failed to count characters in chapter ${chapter.title}"
            }
            return 0L
        }
    }
}