package io.github.lycosmic.lithe.presentation.settings.general

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.lycosmic.data.settings.AppLanguage
import io.github.lycosmic.data.settings.SettingsManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeneralSettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(GeneralSettingsState.DEFAULT)
    val uiState: StateFlow<GeneralSettingsState> = _uiState

    private val _effects = MutableSharedFlow<GeneralSettingsEffect>()
    val effects = _effects.asSharedFlow()

    init {
        observeSettings()
    }

    private fun observeSettings() {
        viewModelScope.launch {
            combine(
                settingsManager.languageCode,
                settingsManager.isDoubleBackToExitEnabled
            ) { language, isDoubleBackToExitEnabled ->
                GeneralSettingsState(
                    appLanguage = language,
                    isDoubleBackToExitEnabled = isDoubleBackToExitEnabled
                )
            }.collectLatest {
                _uiState.value = it
            }
        }
    }

    /**
     * 处理事件
     */
    fun onEvent(event: GeneralSettingsEvent) {
        when (event) {
            // 点击返回按钮
            GeneralSettingsEvent.NavigateBack -> {
                viewModelScope.launch {
                    _effects.emit(GeneralSettingsEffect.NavigateBack)
                }
            }

            // 切换双击返回按钮
            is GeneralSettingsEvent.OnDoubleClickExitClick -> {
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(
                            isDoubleBackToExitEnabled = event.isChecked
                        )
                    }
                    settingsManager.setDoubleBackToExitEnabled(event.isChecked)
                }
            }

            // 语言选择
            is GeneralSettingsEvent.OnLanguageClick -> {
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(
                            appLanguage = event.appLanguage
                        )
                    }
                    settingsManager.setLanguageCode(event.appLanguage)

                    setAppLocale(event.appLanguage)
                }
            }
        }
    }

    /**
     * 调用系统 API 设置语言
     */
    fun setAppLocale(appLanguage: AppLanguage) {
        val localeList = LocaleListCompat.forLanguageTags(appLanguage.code)
        AppCompatDelegate.setApplicationLocales(localeList)
    }

}