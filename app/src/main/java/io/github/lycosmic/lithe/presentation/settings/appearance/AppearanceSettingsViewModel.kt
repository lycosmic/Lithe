package io.github.lycosmic.lithe.presentation.settings.appearance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.lycosmic.lithe.data.model.AppThemeOption
import io.github.lycosmic.lithe.data.model.Constants
import io.github.lycosmic.lithe.data.model.ThemeMode
import io.github.lycosmic.lithe.data.settings.SettingsManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppearanceSettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager
) : ViewModel() {
    /**
     * 主题模式
     */
    val themeMode: Flow<ThemeMode> = settingsManager.themeMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(Constants.STATE_FLOW_STOP_TIMEOUT_MILLIS),
        initialValue = ThemeMode.SYSTEM
    )

    /**
     * 应用主题 ID
     */
    val appThemeId: Flow<String> = settingsManager.appTheme.map { it.id }

    /**
     * 导航栏标签
     */
    val isNavLabelVisible: Flow<Boolean> = settingsManager.showNavigationBarLabels.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(Constants.STATE_FLOW_STOP_TIMEOUT_MILLIS),
        initialValue = true
    )


    private val _effects = MutableSharedFlow<AppearanceSettingsEffect>()
    val effects = _effects.asSharedFlow()

    fun onEvent(event: AppearanceSettingsEvent) {
        viewModelScope.launch {
            when (event) {
                is AppearanceSettingsEvent.OnBackClick -> {
                    _effects.emit(AppearanceSettingsEffect.NavigateBack)
                }

                is AppearanceSettingsEvent.OnThemeModeClick -> {
                    updateThemeMode(event.mode)
                }

                is AppearanceSettingsEvent.OnAppThemeChange -> {
                    updateAppTheme(event.themeId)
                }

                is AppearanceSettingsEvent.OnNavLabelVisibleChange -> {
                    updateNavLabelVisible(event.visible)
                }
            }
        }
    }

    /**
     * 更新主题
     */
    fun updateThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            settingsManager.setThemeMode(mode)
        }
    }

    /**
     * 更新应用主题
     */
    fun updateAppTheme(themeId: String) {
        viewModelScope.launch {
            settingsManager.setAppTheme(AppThemeOption.fromId(themeId))
        }
    }

    /**
     * 更新导航栏标签可见性
     */
    fun updateNavLabelVisible(isVisible: Boolean) {
        viewModelScope.launch {
            settingsManager.setShowNavigationBarLabels(isVisible)
        }
    }
}