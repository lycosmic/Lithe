package io.github.lycosmic.data.counter

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.lycosmic.data.util.CharsetHelper
import io.github.lycosmic.data.util.FileUtils
import io.github.lycosmic.domain.model.Book
import io.github.lycosmic.domain.model.BookChapter
import io.github.lycosmic.domain.util.DomainLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.RandomAccessFile
import java.nio.charset.Charset
import javax.inject.Inject

class TxtCharCounter @Inject constructor(
    @param:ApplicationContext
    private val context: Context,
    private val logger: DomainLogger
) {
    // 返回字符数
    suspend fun count(book: Book, chapter: BookChapter, detectedCharset: String?): Long =
        withContext(
            Dispatchers.IO
        ) {
            val file = FileUtils.getFileFromUri(context, book.fileUri)

            if (file == null || !file.exists()) {
                logger.e { "File not found: ${book.fileUri}" }
                return@withContext 0L
            }

            if (chapter !is BookChapter.TxtChapter) {
                logger.e { "Invalid chapter type: ${chapter::class.simpleName}" }
                return@withContext 0L
            }

            val charsetName = detectedCharset
                ?: book.charset
                ?: CharsetHelper.detectCharset(file).name()

            val charset = try {
                Charset.forName(charsetName)
            } catch (_: Exception) {
                Charsets.UTF_8
            }

            val start = chapter.startOffset
            val length = chapter.endOffset - chapter.startOffset
            if (length <= 0) return@withContext 0L

            return@withContext RandomAccessFile(file, "r").use { raf ->
                raf.seek(start)
                val bytes = ByteArray(length.toInt())
                raf.readFully(bytes)
                String(bytes, charset).length.toLong()
            }
        }
}