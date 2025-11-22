package io.github.lycosmic.lithe.presentation.detail

sealed class BookDetailEvent {

    data object OnBackClicked : BookDetailEvent()
}