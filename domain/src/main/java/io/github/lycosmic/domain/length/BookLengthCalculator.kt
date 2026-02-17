package io.github.lycosmic.domain.length

import io.github.lycosmic.domain.model.Book
import io.github.lycosmic.domain.model.BookChapter


data class CalculationResult(
    val chapters: List<BookChapter>, // 更新了长度的章节
    val charset: String? = null      // TXT 中检测到的字符集
)

interface BookLengthCalculator {
    /**
     * 计算全书精确长度
     * @param book 书籍信息
     * @param chapters 原始章节列表
     */
    suspend fun calculate(book: Book, chapters: List<BookChapter>): CalculationResult
}