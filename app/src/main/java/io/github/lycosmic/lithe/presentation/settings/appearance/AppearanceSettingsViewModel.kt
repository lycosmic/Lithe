package io.github.lycosmic.lithe.presentation.settings.appearance

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.lycosmic.lithe.data.local.dao.ColorPresetDao
import io.github.lycosmic.lithe.data.model.AppThemeOption
import io.github.lycosmic.lithe.data.model.Constants
import io.github.lycosmic.lithe.data.model.ThemeMode
import io.github.lycosmic.lithe.data.settings.SettingsManager
import io.github.lycosmic.lithe.domain.model.ColorPreset
import io.github.lycosmic.lithe.domain.model.ColorPreset.Companion.toEntity
import io.github.lycosmic.lithe.extension.logD
import io.github.lycosmic.lithe.extension.logE
import io.github.lycosmic.lithe.extension.logI
import io.github.lycosmic.lithe.extension.logW
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class AppearanceSettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
    private val colorPresetDao: ColorPresetDao,
) : ViewModel() {
    /**
     * 主题模式
     */
    val themeMode: StateFlow<ThemeMode> = settingsManager.themeMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(Constants.STATE_FLOW_STOP_TIMEOUT_MILLIS),
        initialValue = ThemeMode.SYSTEM
    )

    /**
     * 应用主题 ID
     */
    val appThemeId: StateFlow<String> = settingsManager.appTheme.map { it.id }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(Constants.STATE_FLOW_STOP_TIMEOUT_MILLIS),
        initialValue = AppThemeOption.MERCURY.id
    )

    /**
     * 导航栏标签
     */
    val isNavLabelVisible: StateFlow<Boolean> = settingsManager.showNavigationBarLabels.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(Constants.STATE_FLOW_STOP_TIMEOUT_MILLIS),
        initialValue = true
    )

    /**
     * 数据库中的颜色预设列表
     */
    private val presetsFlow = colorPresetDao.getAllPresets()

    /**
     * 当前选中的颜色预设 ID
     */
    private val selectedIdFlow: Flow<Long> = settingsManager.currentColorPresetId


    /**
     * 颜色预设列表
     */
    val presets: StateFlow<List<ColorPreset>> =
        selectedIdFlow.combine(presetsFlow) { selectedId, presets ->
            presets.sortedBy { it.sortOrder }.map { entity ->
                ColorPreset(
                    id = entity.id ?: 0,
                    name = entity.name,
                    isSelected = entity.id == selectedId,
                    backgroundColor = Color(entity.backgroundColorArgb),
                    textColor = Color(entity.textColorArgb)
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(Constants.STATE_FLOW_STOP_TIMEOUT_MILLIS),
            initialValue = emptyList()
        )

    /**
     * 当前选中的颜色预设
     */
    val currentPreset: StateFlow<ColorPreset?> = presets
        .map { list ->
            list.find { it.isSelected }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(Constants.STATE_FLOW_STOP_TIMEOUT_MILLIS),
            initialValue = null
        )

    init {
        viewModelScope.launch {
            var presetsSync = colorPresetDao.getAllPresetsSync()

            // 如果没有颜色预设，则插入默认的
            if (presetsSync.isEmpty()) {
                val maxSortOrder = colorPresetDao.getMaxSortOrder()
                colorPresetDao.insertPreset(
                    ColorPreset.defaultColorPreset.copy(
                        isSelected = true
                    ).toEntity(
                        maxSortOrder ?: 0
                    )
                )
            }

            // 重新获取颜色预设
            presetsSync = colorPresetDao.getAllPresetsSync()

            // 更新选中的颜色预设
            settingsManager.setCurrentColorPresetId(presetsSync.first().id)
        }
    }

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

                is AppearanceSettingsEvent.OnColorPresetClick -> {
                    settingsManager.setCurrentColorPresetId(event.preset.id)
                }

                is AppearanceSettingsEvent.OnColorPresetDragStopped -> {
                    // 拖拽结束，更新排序
                    logI {
                        "拖拽结束，更新数据库中颜色预设的排序"
                    }

                    val newPresets = event.presets.mapIndexed { index, preset ->
                        preset.toEntity(index)
                    }
                    logD {
                        "新排序为：${newPresets.map { it.name }}"
                    }
                    colorPresetDao.updatePresets(newPresets)
                }

                AppearanceSettingsEvent.OnAddColorPresetClick -> {
                    // 新增颜色预设
                    val maxSortOrder = colorPresetDao.getMaxSortOrder()
                    logD {
                        "新增颜色预设，最大排序为：$maxSortOrder"
                    }
                    colorPresetDao.insertPreset(
                        ColorPreset.defaultColorPreset.copy(
                            isSelected = true
                        ).toEntity(
                            maxSortOrder?.plus(1) ?: 0
                        )
                    )
                }

                is AppearanceSettingsEvent.OnColorPresetNameChange -> {
                    // 修改颜色预设名称
                    val preset = presets.value.find { it.id == event.preset.id } ?: run {
                        logE {
                            "[修改颜色预设的名称] 当前要修改的颜色预设不存在，id: ${event.preset.id}"
                        }
                        return@launch
                    }
                    colorPresetDao.updatePreset(
                        preset.copy(
                            name = event.name
                        ).toEntity(
                            getSortOrder(preset)
                        )
                    )
                }

                is AppearanceSettingsEvent.OnDeleteColorPresetClick -> {
                    if (presets.value.size <= 1) {
                        logW {
                            "[删除颜色预设] 颜色预设列表至少需要有一个颜色预设，无法删除"
                        }
                        return@launch
                    }

                    val preset = presets.value.find { it.id == event.preset.id } ?: run {
                        logE {
                            "[删除颜色预设] 当前要删除的颜色预设不存在，id: ${event.preset.id}"
                        }
                        return@launch
                    }

                    val nextIndex = if (presets.value.lastIndex == presets.value.indexOf(preset)) {
                        presets.value.lastIndex - 1
                    } else {
                        presets.value.indexOf(preset) + 1
                    }

                    colorPresetDao.deletePreset(preset.toEntity(-1))
                    // 选择下一个颜色预设
                    val nextPreset = presets.value[nextIndex]
                    settingsManager.setCurrentColorPresetId(nextPreset.id)
                }

                is AppearanceSettingsEvent.OnShuffleColorPresetClick -> {
                    // 打乱颜色预设的颜色值
                    val preset = presets.value.find { it.id == event.preset.id } ?: run {
                        logE {
                            "[打乱颜色预设的颜色值] 当前要打乱的颜色预设不存在，id: ${event.preset.id}"
                        }
                        return@launch
                    }

                    colorPresetDao.updatePreset(
                        preset.copy(
                            backgroundColor = Color(
                                Random.nextInt(
                                    Constants.MAX_COLOR_VALUE
                                ),
                                Random.nextInt(
                                    Constants.MAX_COLOR_VALUE
                                ),
                                Random.nextInt(
                                    Constants.MAX_COLOR_VALUE
                                )
                            ),
                            textColor = Color(
                                Random.nextInt(
                                    Constants.MAX_COLOR_VALUE
                                ),
                                Random.nextInt(
                                    Constants.MAX_COLOR_VALUE
                                ),
                                Random.nextInt(
                                    Constants.MAX_COLOR_VALUE
                                )
                            )
                        ).toEntity(
                            getSortOrder(preset)
                        )
                    )
                }

                is AppearanceSettingsEvent.OnColorPresetBgColorChange -> {
                    val preset = presets.value.find { it.id == event.preset.id } ?: run {
                        logE {
                            "[修改颜色预设的背景颜色] 当前要修改的颜色预设不存在，id: ${event.preset.id}"
                        }
                        return@launch
                    }
                    colorPresetDao.updatePreset(
                        preset.copy(backgroundColor = event.color).toEntity(
                            getSortOrder(preset)
                        )
                    )
                }

                is AppearanceSettingsEvent.OnColorPresetTextColorChange -> {
                    val preset = presets.value.find { it.id == event.preset.id } ?: run {
                        logE {
                            "[修改颜色预设的文本颜色] 当前要修改的颜色预设不存在，id: ${event.preset.id}"
                        }
                        return@launch
                    }
                    colorPresetDao.updatePreset(
                        preset.copy(textColor = event.color).toEntity(
                            getSortOrder(preset)
                        )
                    )
                }
            }
        }
    }

    /**
     * 获取当前颜色预设的排序
     */
    private suspend fun getSortOrder(preset: ColorPreset): Int {
        return colorPresetDao.getPresetSortOrderById(preset.id)
            ?: colorPresetDao.getMaxSortOrder() ?: 0
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