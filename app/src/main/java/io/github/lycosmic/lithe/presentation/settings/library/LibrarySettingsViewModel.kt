package io.github.lycosmic.lithe.presentation.settings.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibrarySettingsViewModel @Inject constructor() : ViewModel() {


    private val _effects = MutableSharedFlow<LibrarySettingsEffect>()
    val effects = _effects.asSharedFlow()


    fun onEvent(event: LibrarySettingsEvent) {
        when (event) {
            LibrarySettingsEvent.OnBackClick -> {
                viewModelScope.launch(Dispatchers.Default) {
                    _effects.emit(LibrarySettingsEffect.NavigateBack)
                }
            }

            LibrarySettingsEvent.OnCreateCategoryClick -> {

            }
        }
    }
}