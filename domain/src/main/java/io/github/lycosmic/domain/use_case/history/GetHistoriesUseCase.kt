package io.github.lycosmic.domain.use_case.history

import io.github.lycosmic.domain.model.ReadHistory
import io.github.lycosmic.domain.repository.ReadHistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * 根据搜索文本获取历史记录，
 */
class GetHistoriesUseCase @Inject constructor(
    private val historyRepository: ReadHistoryRepository
) {

    suspend operator fun invoke(searchQuery: String): Flow<List<ReadHistory>> {
        val normalizedQuery = searchQuery.lowercase().trim()
        return historyRepository.getHistories()
            .map { histories ->
                histories.filter { history ->
                    history.book.title.lowercase().contains(normalizedQuery)
                }.sortedByDescending { history ->
                    history.time
                }
            }
    }
}