package io.github.lycosmic.lithe.presentation.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.lycosmic.lithe.data.repository.BookRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    // 书籍列表
    val books = bookRepository.getAllBooks().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )


    private val _effects = MutableSharedFlow<LibraryEffect>()
    val effects = _effects.asSharedFlow()

    fun onEvent(event: LibraryEvent) {
        when (event) {
            is LibraryEvent.OnBookClicked -> TODO()
            is LibraryEvent.OnBookLongClicked -> TODO()
            LibraryEvent.OnAboutClicked -> TODO()
            LibraryEvent.OnHelpClicked -> TODO()
            LibraryEvent.OnSettingsClicked -> TODO()
            is LibraryEvent.OnStartReadingClicked -> TODO()
        }
    }

}