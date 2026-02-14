package io.github.lycosmic.lithe.presentation.reader

import androidx.compose.foundation.lazy.LazyListState
import io.github.lycosmic.data.settings.AppChapterTitleAlign
import io.github.lycosmic.data.settings.AppImageAlign
import io.github.lycosmic.data.settings.AppPageAnim
import io.github.lycosmic.data.settings.AppTextAlign
import io.github.lycosmic.data.settings.ImageColorEffect
import io.github.lycosmic.data.settings.ProgressRecord
import io.github.lycosmic.data.settings.ProgressTextAlign
import io.github.lycosmic.data.settings.ScreenOrientation
import io.github.lycosmic.domain.model.AppFontWeight
import io.github.lycosmic.domain.model.ColorPreset

sealed class ReaderEvent {
    // --- 顶部区域 ---
    /**
     * 点击返回按钮
     */
    object OnBackClick : ReaderEvent()

    /**
     * 点击章节菜单按钮
     */
    object OnChapterMenuClick : ReaderEvent()

    /**
     * 切换至上一个颜色预设
     */
    data object OnSwipeLastColorPreset : ReaderEvent()

    /**
     * 切换至下一个颜色预设
     */
    data object OnSwipeNextColorPreset : ReaderEvent()


    // --- 中间区域 ---
    /**
     * 点击阅读内容
     */
    object OnReadContentClick : ReaderEvent()

    /**
     * 内容列表状态发生改变
     */
    data class OnContentListStateChange(val listState: LazyListState) : ReaderEvent()


    /**
     * 用户离开页面，立即保存当前内存中的进度到数据库
     */
    data object OnStopOrDispose : ReaderEvent()


    // --- 底部区域 ---
    /**
     * 点击上一章按钮
     */
    data object OnPrevChapterClick : ReaderEvent()

    /**
     * 点击下一章按钮
     */
    data object OnNextChapterClick : ReaderEvent()


    // --- 章节列表区域 ---
    /**
     * 点击抽屉返回按钮
     */
    object OnDrawerBackClick : ReaderEvent()

    /**
     * 点击章节列表项
     */
    data class OnChapterItemClick(val index: Int) : ReaderEvent()

    // --- 设置相关事件 ---

    // 字体设置
    data class OnFontIdChange(val fontId: String) : ReaderEvent()
    data class OnFontSizeChange(val fontSize: Int) : ReaderEvent()
    data class OnFontWeightChange(val fontWeight: AppFontWeight) : ReaderEvent()
    data class OnItalicChange(val isItalic: Boolean) : ReaderEvent()
    data class OnLetterSpacingChange(val letterSpacing: Int) : ReaderEvent()

    // 文本设置
    data class OnTextAlignChange(val textAlign: AppTextAlign) : ReaderEvent()
    data class OnLineHeightChange(val lineHeight: Int) : ReaderEvent()
    data class OnParagraphSpacingChange(val paragraphSpacing: Int) : ReaderEvent()
    data class OnParagraphIndentChange(val paragraphIndent: Int) : ReaderEvent()

    // 图片设置
    data class OnImageVisibleChange(val isVisible: Boolean) : ReaderEvent()
    data class OnImageCaptionVisibleChange(val isVisible: Boolean) : ReaderEvent()
    data class OnImageColorEffectChange(val effect: ImageColorEffect) : ReaderEvent()
    data class OnImageCornerRadiusChange(val radius: Int) : ReaderEvent()
    data class OnImageAlignChange(val align: AppImageAlign) : ReaderEvent()
    data class OnImageSizePercentChange(val percent: Float) : ReaderEvent()

    // 章节设置
    data class OnChapterTitleAlignChange(val align: AppChapterTitleAlign) : ReaderEvent()

    // 边距设置
    data class OnPageTurnModeChange(val mode: AppPageAnim) : ReaderEvent()
    data class OnSidePaddingChange(val padding: Int) : ReaderEvent()
    data class OnVerticalPaddingChange(val padding: Int) : ReaderEvent()
    data class OnCutoutPaddingApplyChange(val isApply: Boolean) : ReaderEvent()
    data class OnBottomMarginChange(val margin: Int) : ReaderEvent()

    // 进度设置
    data class OnProgressRecordModeChange(val mode: ProgressRecord) : ReaderEvent()
    data class OnProgressBarVisibleChange(val isVisible: Boolean) : ReaderEvent()
    data class OnProgressBarFontSizeChange(val size: Int) : ReaderEvent()
    data class OnProgressBarMarginChange(val margin: Int) : ReaderEvent()
    data class OnProgressBarTextAlignChange(val align: ProgressTextAlign) : ReaderEvent()

    // 颜色预设设置
    data class OnColorPresetClick(val preset: ColorPreset) : ReaderEvent()
    data object OnAddColorPresetClick : ReaderEvent()
    data class OnDeleteColorPresetClick(val preset: ColorPreset) : ReaderEvent()
    data class OnShuffleColorPresetClick(val preset: ColorPreset) : ReaderEvent()
    data class OnColorPresetNameChange(val preset: ColorPreset, val name: String) : ReaderEvent()
    data class OnColorPresetBgColorChange(val preset: ColorPreset, val colorArgb: Int) :
        ReaderEvent()

    data class OnColorPresetTextColorChange(val preset: ColorPreset, val colorArgb: Int) :
        ReaderEvent()

    data class OnColorPresetDragStopped(val presets: List<ColorPreset>) : ReaderEvent()
    data class OnQuickChangeColorPresetEnabledChange(val enabled: Boolean) : ReaderEvent()

    // 系统设置
    data class OnCustomBrightnessEnabledChange(val isEnabled: Boolean) : ReaderEvent()
    data class OnCustomBrightnessValueChange(val value: Float) : ReaderEvent()
    data class OnScreenOrientationChange(val orientation: ScreenOrientation) : ReaderEvent()

    // 杂项设置
    data class OnIsFullScreenChange(val isFullScreen: Boolean) : ReaderEvent()
    data class OnIsKeepScreenOnChange(val isKeep: Boolean) : ReaderEvent()
    data class OnIsHideBarWhenQuickScrollChange(val isHide: Boolean) : ReaderEvent()
}