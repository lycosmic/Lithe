package io.github.lycosmic.domain.use_case.reader

import io.github.lycosmic.domain.model.Book
import io.github.lycosmic.domain.model.BookChapter
import io.github.lycosmic.domain.repository.BookContentParser
import io.github.lycosmic.domain.repository.ChapterRepository
import javax.inject.Inject

/**
 * 获取书籍的章节列表
 */
class GetChapterListUseCase @Inject constructor(
    private val chapterRepository: ChapterRepository,
    private val bookContentParser: BookContentParser
) {
    suspend operator fun invoke(book: Book): Result<List<BookChapter>> {
        return try {
            // 先查数据库缓存
            val cachedChapters = chapterRepository.getChaptersByBookId(book.id)
            if (cachedChapters.isNotEmpty()) {
                // 有缓存，直接返回
                return Result.success(cachedChapters)
            }

            // 解析
            val parsedChapters = bookContentParser.parseChapter(book.fileUri, book.id, book.format)
            parsedChapters.onSuccess { list ->
                chapterRepository.saveChapters(book.id, list)
                Result.success(list)
            }.onFailure { throwable ->
                Result.failure<List<BookChapter>>(throwable)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}