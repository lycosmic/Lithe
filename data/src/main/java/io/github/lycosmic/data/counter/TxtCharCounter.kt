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
import java.io.InputStream
import java.nio.charset.Charset
import javax.inject.Inject

class TxtCharCounter @Inject constructor(
    @param:ApplicationContext
    private val context: Context,
    private val logger: DomainLogger
) {
    // 返回字符数
    suspend fun count(book: Book, chapter: BookChapter, detectedCharset: String?): Long =
        withContext(Dispatchers.IO) {
            try {
                if (chapter !is BookChapter.TxtChapter) {
                    logger.e { "无效的章节类型: ${chapter::class.simpleName}" }
                    return@withContext 0L
                }

                FileUtils.openInputStream(context, book.fileUri)?.use { inputStream ->
                    countTxtChapterCharacters(inputStream, book, chapter, detectedCharset)
                } ?: run {
                    logger.e { "无法打开TXT文件输入流: ${book.fileUri}" }
                    0L
                }
            } catch (e: Exception) {
                logger.e(throwable = e) { "统计TXT章节字符数失败: ${book.fileUri}, 错误: ${e.message}" }
                0L
            }
        }

    private fun countTxtChapterCharacters(
        inputStream: InputStream,
        book: Book,
        chapter: BookChapter.TxtChapter,
        detectedCharset: String?
    ): Long {
        logger.d { "开始统计TXT章节字符数: ${chapter.title}" }

        val charsetName = detectedCharset
            ?: book.charset
            ?: CharsetHelper.detectCharset(inputStream).name()

        val charset = try {
            Charset.forName(charsetName)
        } catch (e: Exception) {
            logger.w(throwable = e) { "无法识别字符集 '$charsetName'，使用UTF-8默认值" }
            Charsets.UTF_8
        }

        val start = chapter.startOffset
        val length = chapter.endOffset - chapter.startOffset

        logger.d { "章节范围: 起始=$start, 长度=$length, 字符集=$charsetName" }

        if (length <= 0) {
            logger.w { "章节长度无效: $length" }
            return 0L
        }

        return inputStream.use { stream ->
            // 跳转到起始位置
            var skipped = 0L
            while (skipped < start) {
                val toSkip = (start - skipped).coerceAtMost(Long.MAX_VALUE)
                val actuallySkipped = stream.skip(toSkip)
                if (actuallySkipped == 0L) break // 无法继续跳转
                skipped += actuallySkipped
            }

            if (skipped != start) {
                logger.e { "无法跳转到指定位置: 期望=$start, 实际=$skipped" }
                return@use 0L
            }

            // 读取指定长度的字节
            val buffer = ByteArray(length.toInt())
            var totalBytesRead = 0
            while (totalBytesRead < length) {
                val count = stream.read(buffer, totalBytesRead, (length - totalBytesRead).toInt())
                if (count == -1) break
                totalBytesRead += count
            }

            if (totalBytesRead > 0) {
                val text = String(buffer, 0, totalBytesRead, charset)
                val charCount = text.length.toLong()
                logger.d { "TXT章节 '${chapter.title}' 字符统计完成: $charCount 字符" }
                charCount
            } else {
                logger.e { "未能读取章节内容" }
                0L
            }
        }
    }
}