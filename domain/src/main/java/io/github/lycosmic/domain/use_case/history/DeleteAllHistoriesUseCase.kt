package io.github.lycosmic.domain.use_case.history

import io.github.lycosmic.domain.repository.ReadHistoryRepository
import javax.inject.Inject


class DeleteAllHistoriesUseCase @Inject constructor(
    private val historyRepository: ReadHistoryRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return historyRepository.deleteWholeHistory()
    }
}