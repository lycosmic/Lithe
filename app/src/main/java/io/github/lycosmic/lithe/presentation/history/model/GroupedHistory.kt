package io.github.lycosmic.lithe.presentation.history.model

import androidx.compose.runtime.Immutable
import io.github.lycosmic.domain.model.ReadHistory

@Immutable
data class GroupedHistory(
    val label: HistoryTimeLabel,
    val histories: List<ReadHistory>
)
