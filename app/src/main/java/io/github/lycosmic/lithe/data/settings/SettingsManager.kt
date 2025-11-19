package io.github.lycosmic.lithe.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.lycosmic.lithe.data.DisplayMode
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
        val IS_DARK_MODE = booleanPreferencesKey("is_dark_mode")

        val FILE_DISPLAY_MODE = stringPreferencesKey("file_display_mode")
    }

    // --- 暗黑模式 ---
    val isDarkMode: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.IS_DARK_MODE] ?: false // 默认亮色模式
    }

    suspend fun setDarkMode(isDarkMode: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.IS_DARK_MODE] = isDarkMode
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
}