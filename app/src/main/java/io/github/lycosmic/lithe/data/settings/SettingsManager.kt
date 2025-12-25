package io.github.lycosmic.lithe.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.lycosmic.lithe.data.model.AppThemeOption
import io.github.lycosmic.lithe.data.model.BookTitlePosition
import io.github.lycosmic.lithe.data.model.BrowseDisplayMode
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

        val FILE_GRID_COLUMN_COUNT = stringPreferencesKey("file_grid_column_count") // 文件网格列数

        val LANGUAGE_CODE = stringPreferencesKey("language_code") // 应用语言代码

        val IS_DOUBLE_BACK_TO_EXIT_ENABLED =
            booleanPreferencesKey("is_double_back_to_exit_enabled") // 是否启用双击返回退出

        val SHOW_NAVIGATION_BAR_LABELS =
            booleanPreferencesKey("show_navigation_bar_labels") // 是否显示导航栏标签

        val CURRENT_COLOR_PRESET_ID = longPreferencesKey("current_color_preset_id") // 当前选中的颜色预设

        val QUICK_CHANGE_COLOR_PRESET =
            booleanPreferencesKey("quick_change_color_preset") // 是否启用快速更改颜色预设

        val BOOK_DISPLAY_MODE = stringPreferencesKey("book_display_mode") // 书籍显示模式

        val BOOK_GRID_COLUMN_COUNT = stringPreferencesKey("book_grid_column_count") // 书籍网格列数

        val BOOK_TITLE_POSITION = stringPreferencesKey("book_title_position") // 书籍标题位置

        val SHOW_READ_BUTTON = booleanPreferencesKey("show_read_button") // 是否显示阅读按钮

        // 是否显示阅读进度
        val SHOW_READ_PROGRESS = booleanPreferencesKey("show_read_progress")

        // 是否显示分类标签页
        val SHOW_CATEGORY_TAB = booleanPreferencesKey("show_category_tab")

        // 是否始终显示默认标签页
        val ALWAYS_SHOW_DEFAULT_CATEGORY_TAB = booleanPreferencesKey("always_show_default_category")

        // 是否显示书籍数
        val SHOW_BOOK_COUNT = booleanPreferencesKey("show_book_count")

        // 是否每个分类都有不同的排序依据
        val DIFFERENT_SORT_ORDER_PER_CATEGORY =
            booleanPreferencesKey("different_sort_order_per_category")
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
    val fileBrowseDisplayMode: Flow<BrowseDisplayMode> = dataStore.data.map { preferences ->
        BrowseDisplayMode.valueOf(
            preferences[Keys.FILE_DISPLAY_MODE] ?: BrowseDisplayMode.List.name
        )
    }

    suspend fun setFileDisplayMode(mode: BrowseDisplayMode) {
        dataStore.edit { preferences ->
            preferences[Keys.FILE_DISPLAY_MODE] = mode.name
        }
    }

    // --- 网格列数 (大小) ---
    val fileGridColumnCount: Flow<Int> = dataStore.data.map { preferences ->
        preferences[Keys.FILE_GRID_COLUMN_COUNT]?.toInt() ?: GRID_COLUMN_COUNT_DEFAULT
    }

    /**
     * 设置网格列数 (大小)
     * @param count 列数
     */
    suspend fun setFileGridColumnCount(count: Int) {
        dataStore.edit { preferences ->
            preferences[Keys.FILE_GRID_COLUMN_COUNT] = count.toString()
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

    // -- 导航栏标签 ---
    val showNavigationBarLabels: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.SHOW_NAVIGATION_BAR_LABELS] ?: true
    }

    /**
     * 设置导航栏标签是否可见
     */
    suspend fun setShowNavigationBarLabels(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.SHOW_NAVIGATION_BAR_LABELS] = isEnabled
        }
    }

    // --- 当前选中的颜色预设 ---
    val currentColorPresetId: Flow<Long> = dataStore.data.map { preferences ->
        preferences[Keys.CURRENT_COLOR_PRESET_ID] ?: -1L
    }

    /**
     * 设置当前选中的颜色预设
     */
    suspend fun setCurrentColorPresetId(id: Long?) {
        dataStore.edit { preferences ->
            preferences[Keys.CURRENT_COLOR_PRESET_ID] = id ?: -1L
        }
    }

    // --- 快速更改颜色预设 ---
    val quickChangeColorPreset: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.QUICK_CHANGE_COLOR_PRESET] ?: false
    }

    /**
     * 设置快速更改颜色预设
     */
    suspend fun setQuickChangeColorPresetEnabled(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.QUICK_CHANGE_COLOR_PRESET] = isEnabled
        }
    }

    // --- 书籍显示模式 ---
    val bookDisplayMode: Flow<BrowseDisplayMode> = dataStore.data.map { preferences ->
        BrowseDisplayMode.valueOf(
            preferences[Keys.BOOK_DISPLAY_MODE] ?: BrowseDisplayMode.List.name
        )
    }

    /**
     * 设置书籍显示模式
     */
    suspend fun setBookDisplayMode(mode: BrowseDisplayMode) {
        dataStore.edit { preferences ->
            preferences[Keys.BOOK_DISPLAY_MODE] = mode.name
        }
    }

    // --- 书籍网格列数 (大小) ---
    val bookGridColumnCount: Flow<Int> = dataStore.data.map { preferences ->
        preferences[Keys.BOOK_GRID_COLUMN_COUNT]?.toInt() ?: GRID_COLUMN_COUNT_DEFAULT
    }

    /**
     * 设置书籍网格列数 (大小)
     */
    suspend fun setBookGridColumnCount(count: Int) {
        dataStore.edit { preferences ->
            preferences[Keys.BOOK_GRID_COLUMN_COUNT] = count.toString()
        }
    }

    // --- 书籍标题位置 ---
    val bookTitlePosition: Flow<BookTitlePosition> = dataStore.data.map { preferences ->
        BookTitlePosition.valueOf(
            preferences[Keys.BOOK_TITLE_POSITION] ?: BookTitlePosition.Below.name
        )
    }

    /**
     * 设置书籍标题位置
     */
    suspend fun setBookTitlePosition(position: BookTitlePosition) {
        dataStore.edit { preferences ->
            preferences[Keys.BOOK_TITLE_POSITION] = position.name
        }
    }

    // --- 显示“阅读”按钮 ---
    val showReadButton: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.SHOW_READ_BUTTON] ?: true
    }

    /**
     * 设置显示“阅读”按钮
     */
    suspend fun setShowReadButton(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.SHOW_READ_BUTTON] = isEnabled
        }
    }

    // --- 显示阅读进度 ---
    val showReadProgress: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.SHOW_READ_PROGRESS] ?: true
    }

    /**
     * 设置是否显示阅读进度
     */
    suspend fun setShowReadProgress(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.SHOW_READ_PROGRESS] = isEnabled
        }
    }

    // --- 显示分类标签页 ---
    val showCategoryTab: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.SHOW_CATEGORY_TAB] ?: true
    }

    /**
     * 显示分类标签页
     */
    suspend fun setShowCategoryTab(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.SHOW_CATEGORY_TAB] = isEnabled
        }
    }

    // --- 是否始终显示默认标签页 ---
    val alwaysShowDefaultCategoryTab: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.ALWAYS_SHOW_DEFAULT_CATEGORY_TAB] ?: true
    }

    /**
     * 设置是否始终显示默认标签页
     */
    suspend fun setAlwaysShowDefaultCategoryTab(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.ALWAYS_SHOW_DEFAULT_CATEGORY_TAB] = isEnabled
        }
    }

    // --- 是否显示书籍数 ---
    val showBookCount: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.SHOW_BOOK_COUNT] ?: true
    }

    /**
     * 设置是否显示书籍数
     */
    suspend fun setShowBookCount(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.SHOW_BOOK_COUNT] = isEnabled
        }
    }

    // --- 是否每个分类都有不同的排序依据 ---
    val eachCategoryHasDifferentSort: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[Keys.DIFFERENT_SORT_ORDER_PER_CATEGORY] ?: true
    }

    /**
     * 设置是否每个分类都有不同的排序依据
     */
    suspend fun setEachCategoryHasDifferentSort(isEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[Keys.DIFFERENT_SORT_ORDER_PER_CATEGORY] = isEnabled
        }
    }

}