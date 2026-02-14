package io.github.lycosmic.domain.use_case.reader

import io.github.lycosmic.domain.repository.ChapterRepository
import io.github.lycosmic.domain.repository.ProgressRepository
import javax.inject.Inject


/**
 * 获取阅读进度（0~1f）
 */
class CalculateProgressPercentUseCase @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val chapterRepository: ChapterRepository
) {
    suspend operator fun invoke(bookId: Long): Result<Float> {
        return runCatching {
            // 1. 获取当前阅读进度
            val readingProgress = progressRepository.getBookProgressSync(bookId).getOrThrow()
            val currentChapterIndex = readingProgress.chapterIndex
            val currentChapterProgress = readingProgress.progressPercent

            // 2. 获取书籍章节
            val chapters = chapterRepository.getChaptersByBookId(bookId).getOrThrow()
            val currentChapter = chapters[currentChapterIndex]

            // 3. 获取当前章节已读的长度
            val currentChapterLength = currentChapter.length
            val currentChapterContribution = currentChapterLength * currentChapterProgress

            // 4. 获取书籍总长度
            val totalBookLength = chapters.sumOf { chapter ->
                chapter.length
            }

            // 5. 获取前 N 章的长度
            val previousChapterLength = chapters.take(currentChapterIndex).sumOf { chapter ->
                chapter.length
            }

            // 5. 计算总进度 = 前 N 章长度 + 当前章节已读长度 / 全书总长度
            // 其中当前章节的“字节贡献” = 当前章总字节 * 当前章阅读百分比
            return@runCatching (previousChapterLength + currentChapterContribution) / totalBookLength
        }
    }
}