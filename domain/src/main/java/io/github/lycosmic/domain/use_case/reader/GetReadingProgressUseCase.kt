package io.github.lycosmic.domain.use_case.reader

import io.github.lycosmic.domain.model.ReadingProgress
import io.github.lycosmic.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 获取阅读进度
 */
class GetReadingProgressUseCase @Inject constructor(
    private val progressRepository: ProgressRepository
) {
    suspend operator fun invoke(bookId: Long): Flow<Result<ReadingProgress>> {
        return progressRepository.getBookProgress(bookId)
    }
}