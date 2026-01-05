package io.github.lycosmic.domain.use_case.reader

import io.github.lycosmic.domain.model.ReadingProgress
import io.github.lycosmic.domain.repository.ProgressRepository
import javax.inject.Inject


class SaveReadingProgressUseCase @Inject constructor(
    private val progressRepository: ProgressRepository
) {
    suspend operator fun invoke(
        bookId: Long,
        progress: ReadingProgress
    ): Result<Any> {
        return progressRepository.saveProgress(bookId, progress)
    }
}