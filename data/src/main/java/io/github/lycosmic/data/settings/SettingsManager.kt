package io.github.lycosmic.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.lycosmic.data.util.getEnum
import io.github.lycosmic.data.util.getValue
import io.github.lycosmic.data.util.setEnum
import io.github.lycosmic.data.util.setValue
import io.github.lycosmic.domain.model.AppFontFamily
import io.github.lycosmic.domain.model.AppFontWeight
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 设置中心
 */
class SettingsManager @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    // DataStore Keys
    private object Keys {
        // 主题模式
        val THEME_MODE = stringPreferencesKey("theme")

        // 应用主题
        val APP_THEME = stringPreferencesKey("app_theme")

        // 文件显示模式
        val FILE_DISPLAY_MODE = stringPreferencesKey("file_display_mode")

        // 文件网格列数
        val FILE_GRID_COLUMN_COUNT = intPreferencesKey("file_grid_column_count")

        // 应用语言
        val APP_LANGUAGE = stringPreferencesKey("app_language")

        // 是否启用双击返回退出
        val IS_DOUBLE_BACK_TO_EXIT_ENABLED =
            booleanPreferencesKey("is_double_back_to_exit_enabled")

        // 是否显示导航栏标签
        val SHOW_NAVIGATION_BAR_LABELS =
            booleanPreferencesKey("show_navigation_bar_labels")

        // 当前选中的颜色预设
        val CURRENT_COLOR_PRESET_ID = longPreferencesKey("current_color_preset_id")

        // 是否启用快速更改颜色预设
        val QUICK_CHANGE_COLOR_PRESET =
            booleanPreferencesKey("quick_change_color_preset")

        // 书籍显示模式
        val BOOK_DISPLAY_MODE = stringPreferencesKey("book_display_mode")

        // 书籍网格列数
        val BOOK_GRID_COLUMN_COUNT = intPreferencesKey("book_grid_column_count")

        // 书籍标题位置
        val BOOK_TITLE_POSITION = stringPreferencesKey("book_title_position")

        // 是否显示阅读按钮
        val SHOW_READ_BUTTON = booleanPreferencesKey("show_read_button")

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

        // 书籍排序依据
        val BOOK_SORT_TYPE = stringPreferencesKey("book_sort_type")

        // 书籍排序顺序
        val BOOK_SORT_ORDER = booleanPreferencesKey("book_sort_order")

        // 阅读字体ID
        val READER_FONT_ID = stringPreferencesKey("reader_font_id")

        // 阅读字体大小
        val READER_FONT_SIZE = intPreferencesKey("reader_font_size")

        // 阅读字体样式
        val READER_FONT_WEIGHT = stringPreferencesKey("app_font_weight")

        // 阅读字体是否斜体
        val READER_IS_ITALIC = booleanPreferencesKey("reader_is_italic")

        // 阅读字间距
        val READER_LETTER_SPACING = intPreferencesKey("reader_letter_spacing")

        // 文本对齐方式
        val READER_TEXT_ALIGN = stringPreferencesKey("reader_text_align")

        // 行高
        val READER_LINE_HEIGHT = intPreferencesKey("reader_line_height")

        // 段落间距
        val READER_PARAGRAPH_SPACING = intPreferencesKey("reader_para_spacing")

        // 段落缩进
        val READER_PARAGRAPH_INDENT = intPreferencesKey("reader_paragraph_indent")

        // 图片显示总开关
        val READER_IMAGE_ENABLED = booleanPreferencesKey("reader_image_enabled")

        // 是否显示图片说明文字
        val READER_IMAGE_CAPTION_ENABLED = booleanPreferencesKey("reader_image_caption_enabled")

        // 颜色效果
        val READER_IMAGE_COLOR_EFFECT = stringPreferencesKey("reader_image_color_effect")

        // 图片边角圆度
        val READER_IMAGE_CORNER_RADIUS = intPreferencesKey("reader_image_corner_radius")

        // 图片对齐方式
        val READER_IMAGE_ALIGN = stringPreferencesKey("reader_image_align")

        // 图片尺寸百分比
        val READER_IMAGE_SIZE_PERCENT = floatPreferencesKey("reader_image_size_percent")

        // 章节标题对齐
        val READER_CHAPTER_TITLE_ALIGN = stringPreferencesKey("reader_chapter_title_align")

        // 阅读模式
        val READER_MODE = stringPreferencesKey("reader_mode")

        // 侧边填充
        val READER_SIDE_PADDING = intPreferencesKey("reader_side_padding")

        // 垂直填充
        val READER_VERTICAL_PADDING = intPreferencesKey("reader_vertical_padding")

        // 刘海边距
        val READER_CUTOUT_PADDING = booleanPreferencesKey("reader_cutout_padding")

        // 底部边距
        val READER_BOTTOM_BAR_PADDING = intPreferencesKey("reader_bottom_bar_padding")

        // 是否开启自定义亮度
        val READER_CUSTOM_BRIGHTNESS_ENABLED =
            booleanPreferencesKey("reader_custom_brightness_enabled")

        // 自定义亮度
        val READER_CUSTOM_BRIGHTNESS = floatPreferencesKey("reader_custom_brightness")

        // 屏幕方向
        val SCREEN_ORIENTATION = stringPreferencesKey("screen_orientation")

        // 是否显示底部的进度文字
        val SHOW_PROGRESS_TEXT = booleanPreferencesKey("show_bottom_progress_text")

        // 进度条文本字体大小
        val PROGRESS_RECORD_FONT_SIZE = intPreferencesKey("progress_record_font_size")

        // 进度条边距
        val PROGRESS_RECORD_MARGIN = intPreferencesKey("progress_record_margin")

        // 进度条文本对齐方式
        val PROGRESS_RECORD_TEXT_ALIGN = stringPreferencesKey("progress_record_text_align")

        // 是否开启全屏
        val IS_FULL_SCREEN_ENABLED = booleanPreferencesKey("is_full_screen_enabled")

        // 是否保持屏幕常亮
        val KEEP_SCREEN_ON = booleanPreferencesKey("keep_screen_on")

        // 快速滚动时，是否隐藏上下栏
        val HIDE_BAR_WHEN_QUICK_SCROLL =
            booleanPreferencesKey("hide_bar_when_quick_scroll")
    }

    companion object {
        const val GRID_COLUMN_COUNT_DEFAULT = 3 // 默认 3 列
        const val DEFAULT_FONT_SIZE = 18
        const val DEFAULT_LETTER_SPACING = 0

        // 默认的行高
        const val DEFAULT_LINE_HEIGHT = 4

        // 默认的段落间距
        const val DEFAULT_PARAGRAPH_SPACING = 1

        // 默认的段落缩进
        const val DEFAULT_PARAGRAPH_INDENT = 0

        // 默认的图片边角圆度
        const val DEFAULT_IMAGE_CORNER_RADIUS = 0

        // 默认的图片尺寸百分比
        const val DEFAULT_IMAGE_SIZE_PERCENT = 100f
    }


    // --- 主题模式 ---
    val themeMode = dataStore.getEnum(Keys.THEME_MODE, ThemeMode.DEFAULT_THEME_MODE)
    suspend fun setThemeMode(mode: ThemeMode) = dataStore.setEnum(Keys.THEME_MODE, mode)

    // --- 文件显示模式 ---
    val fileDisplayMode: Flow<DisplayMode> =
        dataStore.getEnum(Keys.FILE_DISPLAY_MODE, DisplayMode.List)

    suspend fun setFileDisplayMode(mode: DisplayMode) =
        dataStore.setEnum(Keys.FILE_DISPLAY_MODE, mode)

    // --- 网格列数 (大小) ---
    val fileGridColumnCount: Flow<Int> =
        dataStore.getValue(Keys.FILE_GRID_COLUMN_COUNT, GRID_COLUMN_COUNT_DEFAULT)

    suspend fun setFileGridColumnCount(count: Int) =
        dataStore.setValue(Keys.FILE_GRID_COLUMN_COUNT, count)

    // --- 应用语言 ---
    val appLanguage: Flow<AppLanguage> = dataStore.getEnum(Keys.APP_LANGUAGE, AppLanguage.CHINESE)
    suspend fun setAppLanguage(language: AppLanguage) =
        dataStore.setEnum(Keys.APP_LANGUAGE, language)

    // --- 双击返回退出 ---
    val isDoubleBackToExitEnabled: Flow<Boolean> =
        dataStore.getValue(Keys.IS_DOUBLE_BACK_TO_EXIT_ENABLED, false)

    suspend fun setDoubleBackToExitEnabled(isEnabled: Boolean) =
        dataStore.setValue(Keys.IS_DOUBLE_BACK_TO_EXIT_ENABLED, isEnabled)

    // --- 应用主题 ---
    val appTheme: Flow<AppThemeOption> = dataStore.getEnum(Keys.APP_THEME, AppThemeOption.MERCURY)
    suspend fun setAppTheme(theme: AppThemeOption) = dataStore.setEnum(Keys.APP_THEME, theme)

    // -- 导航栏标签 ---
    val showNavigationBarLabels: Flow<Boolean> =
        dataStore.getValue(Keys.SHOW_NAVIGATION_BAR_LABELS, true)

    suspend fun setShowNavigationBarLabels(isEnabled: Boolean) =
        dataStore.setValue(Keys.SHOW_NAVIGATION_BAR_LABELS, isEnabled)

    // --- 颜色预设 ---
    val currentColorPresetId: Flow<Long> = dataStore.getValue(Keys.CURRENT_COLOR_PRESET_ID, -1L)
    suspend fun setCurrentColorPresetId(id: Long) =
        dataStore.setValue(Keys.CURRENT_COLOR_PRESET_ID, id)

    // --- 是否快速更改颜色预设 ---
    val quickChangeColorPreset: Flow<Boolean> =
        dataStore.getValue(Keys.QUICK_CHANGE_COLOR_PRESET, false)

    suspend fun setQuickChangeColorPresetEnabled(isEnabled: Boolean) =
        dataStore.setValue(Keys.QUICK_CHANGE_COLOR_PRESET, isEnabled)

    // --- 书籍显示模式 ---
    val bookDisplayMode: Flow<DisplayMode> =
        dataStore.getEnum(Keys.BOOK_DISPLAY_MODE, DisplayMode.List)

    suspend fun setBookDisplayMode(mode: DisplayMode) =
        dataStore.setEnum(Keys.BOOK_DISPLAY_MODE, mode)

    // --- 书籍网格列数 (大小) ---
    val bookGridColumnCount: Flow<Int> =
        dataStore.getValue(Keys.BOOK_GRID_COLUMN_COUNT, GRID_COLUMN_COUNT_DEFAULT)

    suspend fun setBookGridColumnCount(count: Int) =
        dataStore.setValue(Keys.BOOK_GRID_COLUMN_COUNT, count)

    // --- 书籍标题位置 ---
    val bookTitlePosition: Flow<BookTitlePosition> =
        dataStore.getEnum(Keys.BOOK_TITLE_POSITION, BookTitlePosition.Below)

    suspend fun setBookTitlePosition(position: BookTitlePosition) =
        dataStore.setEnum(Keys.BOOK_TITLE_POSITION, position)

    // --- 显示“阅读”按钮 ---
    val showReadButton: Flow<Boolean> = dataStore.getValue(Keys.SHOW_READ_BUTTON, true)
    suspend fun setShowReadButton(isEnabled: Boolean) =
        dataStore.setValue(Keys.SHOW_READ_BUTTON, isEnabled)

    // --- 显示阅读进度 ---
    val showReadProgress: Flow<Boolean> = dataStore.getValue(Keys.SHOW_READ_PROGRESS, true)
    suspend fun setShowReadProgress(isEnabled: Boolean) =
        dataStore.setValue(Keys.SHOW_READ_PROGRESS, isEnabled)

    // --- 显示分类标签页 ---
    val showCategoryTab: Flow<Boolean> = dataStore.getValue(Keys.SHOW_CATEGORY_TAB, true)
    suspend fun setShowCategoryTab(isEnabled: Boolean) =
        dataStore.setValue(Keys.SHOW_CATEGORY_TAB, isEnabled)

    // --- 是否始终显示默认标签页 ---
    val alwaysShowDefaultCategoryTab: Flow<Boolean> =
        dataStore.getValue(Keys.ALWAYS_SHOW_DEFAULT_CATEGORY_TAB, true)

    suspend fun setAlwaysShowDefaultCategoryTab(isEnabled: Boolean) =
        dataStore.setValue(Keys.ALWAYS_SHOW_DEFAULT_CATEGORY_TAB, isEnabled)

    // --- 是否显示书籍数 ---
    val showBookCount: Flow<Boolean> = dataStore.getValue(Keys.SHOW_BOOK_COUNT, true)
    suspend fun setShowBookCount(isEnabled: Boolean) =
        dataStore.setValue(Keys.SHOW_BOOK_COUNT, isEnabled)

    // --- 是否每个分类都有不同的排序依据 ---
    val eachCategoryHasDifferentSort: Flow<Boolean> =
        dataStore.getValue(Keys.DIFFERENT_SORT_ORDER_PER_CATEGORY, true)

    suspend fun setEachCategoryHasDifferentSort(isEnabled: Boolean) =
        dataStore.setValue(Keys.DIFFERENT_SORT_ORDER_PER_CATEGORY, isEnabled)

    // --- 书籍排序方式 ---
    val bookSortType: Flow<BookSortType> =
        dataStore.getEnum(Keys.BOOK_SORT_TYPE, BookSortType.ALPHABETICAL)

    suspend fun setBookSortType(type: BookSortType) = dataStore.setEnum(Keys.BOOK_SORT_TYPE, type)

    // --- 书籍排序是否升序 ---
    val bookSortOrder: Flow<Boolean> = dataStore.getValue(Keys.BOOK_SORT_ORDER, false)
    suspend fun setBookSortOrder(isAscending: Boolean) =
        dataStore.setValue(Keys.BOOK_SORT_ORDER, isAscending)

    // --- 阅读器字体ID ---
    val readerFontId: Flow<String> =
        dataStore.getValue(Keys.READER_FONT_ID, AppFontFamily.Default.id)

    suspend fun setReaderFontId(id: String) = dataStore.setValue(Keys.READER_FONT_ID, id)

    // --- 阅读器字体大小 ---
    val readerFontSize: Flow<Int> =
        dataStore.getValue(Keys.READER_FONT_SIZE, DEFAULT_FONT_SIZE)

    suspend fun setReaderFontSize(size: Int) = dataStore.setValue(Keys.READER_FONT_SIZE, size)

    // --- 阅读器字体粗细 ---
    val appFontWeight: Flow<AppFontWeight> =
        dataStore.getEnum(Keys.READER_FONT_WEIGHT, AppFontWeight.Normal)

    suspend fun setReaderFontWeight(weight: AppFontWeight) =
        dataStore.setEnum(Keys.READER_FONT_WEIGHT, weight)

    // --- 字体是否是斜体 ---
    val isReaderItalic: Flow<Boolean> = dataStore.getValue(Keys.READER_IS_ITALIC, false)
    suspend fun setReaderItalic(isItalic: Boolean) =
        dataStore.setValue(Keys.READER_IS_ITALIC, isItalic)

    // --- 阅读器字间距 ---
    val readerLetterSpacing: Flow<Int> =
        dataStore.getValue(Keys.READER_LETTER_SPACING, DEFAULT_LETTER_SPACING)

    suspend fun setReaderLetterSpacing(spacing: Int) =
        dataStore.setValue(Keys.READER_LETTER_SPACING, spacing)


    // --- 文本对齐方式 ---
    val readerTextAlign: Flow<AppTextAlign> =
        dataStore.getEnum(Keys.READER_TEXT_ALIGN, AppTextAlign.JUSTIFY)

    suspend fun setReaderTextAlign(align: AppTextAlign) =
        dataStore.setEnum(Keys.READER_TEXT_ALIGN, align)

    // --- 行高 ---
    val readerLineHeight: Flow<Int> =
        dataStore.getValue(Keys.READER_LINE_HEIGHT, DEFAULT_LINE_HEIGHT)

    suspend fun setReaderLineHeight(height: Int) =
        dataStore.setValue(Keys.READER_LINE_HEIGHT, height)

    // --- 段落间距 ---
    val readerParagraphSpacing: Flow<Int> =
        dataStore.getValue(Keys.READER_PARAGRAPH_SPACING, DEFAULT_PARAGRAPH_SPACING)

    suspend fun setReaderParagraphSpacing(spacing: Int) =
        dataStore.setValue(Keys.READER_PARAGRAPH_SPACING, spacing)

    // --- 段落缩进 ---
    val readerParagraphIndent: Flow<Int> =
        dataStore.getValue(Keys.READER_PARAGRAPH_INDENT, DEFAULT_PARAGRAPH_INDENT)

    suspend fun setReaderParagraphIndent(indent: Int) =
        dataStore.setValue(Keys.READER_PARAGRAPH_INDENT, indent)


    // --- 图片是否显示 ---
    val readerImageEnabled: Flow<Boolean> = dataStore.getValue(Keys.READER_IMAGE_ENABLED, true)
    suspend fun setReaderImageEnabled(isEnabled: Boolean) =
        dataStore.setValue(Keys.READER_IMAGE_ENABLED, isEnabled)

    // --- 是否显示图片说明 ---
    val readerImageCaptionEnabled: Flow<Boolean> =
        dataStore.getValue(Keys.READER_IMAGE_CAPTION_ENABLED, true)

    suspend fun setReaderImageCaptionEnabled(isEnabled: Boolean) =
        dataStore.setValue(Keys.READER_IMAGE_CAPTION_ENABLED, isEnabled)

    // --- 图片颜色效果 ---
    val readerImageColorEffect: Flow<ImageColorEffect> =
        dataStore.getEnum(Keys.READER_IMAGE_COLOR_EFFECT, ImageColorEffect.NONE)

    suspend fun setReaderImageColorEffect(effect: ImageColorEffect) =
        dataStore.setEnum(Keys.READER_IMAGE_COLOR_EFFECT, effect)

    // --- 图片边角圆度 ---
    val readerImageCornerRadius: Flow<Int> = dataStore.getValue(Keys.READER_IMAGE_CORNER_RADIUS, 8)
    suspend fun setReaderImageCornerRadius(radius: Int) =
        dataStore.setValue(Keys.READER_IMAGE_CORNER_RADIUS, radius)

    // --- 图片对齐方式 ---
    val readerImageAlign: Flow<AppImageAlign> =
        dataStore.getEnum(Keys.READER_IMAGE_ALIGN, AppImageAlign.CENTER)

    suspend fun setReaderImageAlign(align: AppImageAlign) =
        dataStore.setEnum(Keys.READER_IMAGE_ALIGN, align)

    // --- 图片大小百分比 ---
    val readerImageSizePercent: Flow<Float> =
        dataStore.getValue(Keys.READER_IMAGE_SIZE_PERCENT, 100f)

    suspend fun setReaderImageSizePercent(percent: Float) =
        dataStore.setValue(Keys.READER_IMAGE_SIZE_PERCENT, percent)

    // --- 章节标题对齐方式 ---
    val readerChapterTitleAlign: Flow<AppChapterTitleAlign> = dataStore.getEnum(
        Keys.READER_CHAPTER_TITLE_ALIGN, AppChapterTitleAlign.START
    )

    suspend fun setReaderChapterTitleAlign(align: AppChapterTitleAlign) =
        dataStore.setEnum(Keys.READER_CHAPTER_TITLE_ALIGN, align)

    // --- 阅读模式 ---
    val readerMode: Flow<ReadingMode> = dataStore.getEnum(Keys.READER_MODE, ReadingMode.SCROLL)
    suspend fun setReaderMode(mode: ReadingMode) = dataStore.setEnum(Keys.READER_MODE, mode)

    // --- 侧边距 ---
    val readerSidePadding: Flow<Int> = dataStore.getValue(Keys.READER_SIDE_PADDING, 1)
    suspend fun setReaderSidePadding(padding: Int) =
        dataStore.setValue(Keys.READER_SIDE_PADDING, padding)

    // --- 垂直边距 ---
    val readerVerticalPadding: Flow<Int> = dataStore.getValue(Keys.READER_VERTICAL_PADDING, 0)
    suspend fun setReaderVerticalPadding(padding: Int) =
        dataStore.setValue(Keys.READER_VERTICAL_PADDING, padding)

    // --- 是否应用刘海边距 ---
    val readerCutoutPaddingApply: Flow<Boolean> =
        dataStore.getValue(Keys.READER_CUTOUT_PADDING, false)

    suspend fun setReaderCutoutPaddingApply(isApply: Boolean) =
        dataStore.setValue(Keys.READER_CUTOUT_PADDING, isApply)

    // --- 底部栏边距 ---
    val readerBottomBarPadding: Flow<Int> = dataStore.getValue(Keys.READER_BOTTOM_BAR_PADDING, 0)
    suspend fun setReaderBottomMargin(margin: Int) =
        dataStore.setValue(Keys.READER_BOTTOM_BAR_PADDING, margin)

    // --- 是否自定义亮度 ---
    val readerCustomBrightnessEnabled: Flow<Boolean> =
        dataStore.getValue(Keys.READER_CUSTOM_BRIGHTNESS_ENABLED, false)

    suspend fun setReaderCustomBrightnessEnabled(isEnabled: Boolean) =
        dataStore.setValue(Keys.READER_CUSTOM_BRIGHTNESS_ENABLED, isEnabled)

    // --- 自定义亮度 ---
    val readerCustomBrightness: Flow<Float> =
        dataStore.getValue(Keys.READER_CUSTOM_BRIGHTNESS, 0.5f)

    suspend fun setReaderCustomBrightness(brightness: Float) =
        dataStore.setValue(Keys.READER_CUSTOM_BRIGHTNESS, brightness)

    // --- 屏幕方向 ---
    val screenOrientation: Flow<ScreenOrientation> =
        dataStore.getEnum(Keys.SCREEN_ORIENTATION, ScreenOrientation.DEFAULT)

    suspend fun setScreenOrientation(orientation: ScreenOrientation) =
        dataStore.setEnum(Keys.SCREEN_ORIENTATION, orientation)

    // --- 是否显示底部的进度文字 ---
    val showProgressText: Flow<Boolean> = dataStore.getValue(Keys.SHOW_PROGRESS_TEXT, false)
    suspend fun setShowProgressBar(isShow: Boolean) =
        dataStore.setValue(Keys.SHOW_PROGRESS_TEXT, isShow)

    // --- 进度条字体大小 ---
    val progressBarFontSize: Flow<Int> = dataStore.getValue(Keys.PROGRESS_RECORD_FONT_SIZE, 4)

    suspend fun setProgressBarFontSize(size: Int) =
        dataStore.setValue(Keys.PROGRESS_RECORD_FONT_SIZE, size)

    // --- 进度条字体边距 ---
    val progressBarMargin: Flow<Int> =
        dataStore.getValue(Keys.PROGRESS_RECORD_MARGIN, 0)

    suspend fun setProgressBarMargin(margin: Int) =
        dataStore.setValue(Keys.PROGRESS_RECORD_MARGIN, margin)

    // --- 进度条文本对齐方式 ---
    val progressBarTextAlign: Flow<ProgressTextAlign> = dataStore.getEnum(
        Keys.PROGRESS_RECORD_TEXT_ALIGN, ProgressTextAlign.START
    )

    suspend fun setProgressBarTextAlign(align: ProgressTextAlign) =
        dataStore.setEnum(Keys.PROGRESS_RECORD_TEXT_ALIGN, align)

    // --- 是否全屏 ---
    val isFullScreenEnabled: Flow<Boolean> = dataStore.getValue(Keys.IS_FULL_SCREEN_ENABLED, true)
    suspend fun setIsFullScreenEnabled(isEnabled: Boolean) =
        dataStore.setValue(Keys.IS_FULL_SCREEN_ENABLED, isEnabled)

    // --- 是否保持屏幕常亮 ---
    val keepScreenOn: Flow<Boolean> = dataStore.getValue(Keys.KEEP_SCREEN_ON, true)
    suspend fun setKeepScreenOn(isKeep: Boolean) = dataStore.setValue(Keys.KEEP_SCREEN_ON, isKeep)

    // --- 是否隐藏上下栏当快速滑动 ---
    val hideBarWhenQuickScroll: Flow<Boolean> =
        dataStore.getValue(Keys.HIDE_BAR_WHEN_QUICK_SCROLL, true)

    suspend fun setHideBarWhenQuickScroll(isHide: Boolean) =
        dataStore.setValue(Keys.HIDE_BAR_WHEN_QUICK_SCROLL, isHide)
}