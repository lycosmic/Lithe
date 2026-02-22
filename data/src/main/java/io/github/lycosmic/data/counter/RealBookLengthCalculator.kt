package io.github.lycosmic.data.counter

import androidx.core.net.toUri
import io.github.lycosmic.data.util.CharsetHelper
import io.github.lycosmic.domain.length.BookLengthCalculator
import io.github.lycosmic.domain.length.CalculationResult
import io.github.lycosmic.domain.model.Book
import io.github.lycosmic.domain.model.BookChapter
import io.github.lycosmic.domain.model.FileFormat
import java.io.File
import javax.inject.Inject

class RealBookLengthCalculator @Inject constructor(
    private val epubCounter: EpubCharCounter,
    private val txtCounter: TxtCharCounter
) : BookLengthCalculator {

    override suspend fun calculate(book: Book, chapters: List<BookChapter>): CalculationResult {
        // 1. 如果是 TXT 且没有编码，先检测一次
        var charsetToUse: String? = book.charset
        if (book.format == FileFormat.TXT && charsetToUse == null) {
            val file = File(book.fileUri.toUri().path!!)
            charsetToUse = CharsetHelper.detectCharset(file).name()
        }

        val newChapters = mutableListOf<BookChapter>()

        // 2. 遍历计算
        for (chapter in chapters) {
            if (chapter.realCharCount > 0) {
                continue
            }

            val realLength = when (book.format) {
                FileFormat.EPUB -> epubCounter.count(book, chapter)
                // 传进去我们确定好的编码
                FileFormat.TXT -> txtCounter.count(book, chapter, charsetToUse)
                else -> {
                    throw IllegalArgumentException("Unsupported file format: ${book.format}")
                }
            }

            if (realLength <= 0L) {
                continue
            }

            val newChapter = when (chapter) {
                is BookChapter.EpubChapter -> {
                    chapter.copy(realCharCount = realLength)
                }

                is BookChapter.TxtChapter -> {
                    chapter.copy(realCharCount = realLength)
                }
            }
            newChapters.add(newChapter)
        }

        // 3. 返回结果
        return CalculationResult(
            chapters = newChapters,
            charset = charsetToUse
        )
    }
}