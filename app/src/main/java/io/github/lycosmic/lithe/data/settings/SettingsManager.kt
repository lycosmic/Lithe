package io.github.lycosmic.lithe.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.lycosmic.lithe.data.model.AppThemeOption
import io.github.lycosmic.lithe.data.model.DisplayMode
import io.github.lycosmic.lithe.data.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * 设置中心
 */
class SettingsManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    // DataStore Keys
    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme") // 主题模式

        val APP_THEME = stringPreferencesKey("app_theme") // 应用主题

        val FILE_DISPLAY_MODE = stringPreferencesKey("file_display_mode") // 文件显示模式

        val GRID_COLUMN_COUNT = stringPreferencesKey("grid_column_count") // 文件网格列数

        val LANGUAGE_CODE = stringPreferencesKey("language_code") // 应用语言代码

        val IS_DOUBLE_BACK_TO_EXIT_ENABLED =
            booleanPreferencesKey("is_double_back_to_exit_enabled") // 是否启用双击返回退出
    }

    companion object {
        const val GRID_COLUMN_COUNT_DEFAULT = 3 // 默认 3 列
        const val LANGUAGE_CODE_DEFAULT = "zh-CN"
        const val THEME_MODE_DEFAULT = "system"
    }

    // --- 主题模式 ---
    val themeMode: Flow<ThemeMode> = dataStore.data.map { preferences ->
        ThemeMode.fromValue(
            preferences[Keys.THEME_MODE] ?: THEME_MODE_DEFAULT // 默认跟随系统
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { preferences ->
            preferences[Keys.THEME_MODE] = mode.value
        }
    }

    // --- 文件显示模式 ---
    val fileDisplayMode: Flow<DisplayMode> = dataStore.data.map { preferences ->
        DisplayMode.valueOf(
            preferences[Keys.FILE_DISPLAY_MODE] ?: DisplayMode.List.name
        )
    }

    suspend fun setFileDisplayMode(mode: DisplayMode) {
        dataStore.edit { preferences ->
            preferences[Keys.FILE_DISPLAY_MODE] = mode.name
        }
    }

    // --- 网格列数 (大小) ---
    val gridColumnCount: Flow<Int> = dataStore.data.map { preferences ->
        preferences[Keys.GRID_COLUMN_COUNT]?.toInt() ?: GRID_COLUMN_COUNT_DEFAULT
    }

    /**
     * 设置网格列数 (大小)
     * @param count 列数
     */
    suspend fun setGridColumnCount(count: Int) {
        dataStore.edit { preferences ->
            preferences[Keys.GRID_COLUMN_COUNT] = count.toString()
        }
    }

    // --- 应用语言 ---
    val languageCode: Flow<String> = dataStore.data.map { preferences ->
        preferences[Keys.LANGUAGE_CODE] ?: LANGUAGE_CODE_DEFAULT
    }

    /**
     * 设置应用语言代码
     */
    suspend fun setLanguageCode(code: String) {
        dataStore.edit { preferences ->
            preferences[Keys.LANGUAGE_CODE] = code
        }
    }

    // --- 双击返回退出 ---
    val isDoubleBackToExitEnabled: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.IS_DOUBLE_BACK_TO_EXIT_ENABLED] ?: false
    }

    /**
     * 设置双击返回退出
     */
    suspend fun setDoubleBackToExitEnabled(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.IS_DOUBLE_BACK_TO_EXIT_ENABLED] = isEnabled
        }
    }

    // --- 应用主题 ---
    val appTheme: Flow<AppThemeOption> = dataStore.data.map { preferences ->
        AppThemeOption.fromId(
            preferences[Keys.APP_THEME] ?: AppThemeOption.MERCURY.id
        )
    }

    /**
     * 设置应用主题
     */
    suspend fun setAppTheme(theme: AppThemeOption) {
        dataStore.edit { preferences ->
            preferences[Keys.APP_THEME] = theme.id
        }
    }

}