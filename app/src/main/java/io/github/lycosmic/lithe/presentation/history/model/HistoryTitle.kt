package io.github.lycosmic.lithe.presentation.history.model


sealed class HistoryTimeLabel {
    data object Today : HistoryTimeLabel()
    data object Yesterday : HistoryTimeLabel()
    data class Other(val date: String) : HistoryTimeLabel()
}