package io.github.lycosmic.domain.use_case.history

import io.github.lycosmic.domain.model.ReadHistory
import io.github.lycosmic.domain.repository.ReadHistoryRepository
import javax.inject.Inject


/**
 * 添加阅读记录
 */
class AddHistoryUseCase @Inject constructor(
    private val historyRepository: ReadHistoryRepository
) {
    suspend operator fun invoke(
        history: ReadHistory
    ): Result<Unit> {
        return historyRepository.addHistory(history)
    }
}