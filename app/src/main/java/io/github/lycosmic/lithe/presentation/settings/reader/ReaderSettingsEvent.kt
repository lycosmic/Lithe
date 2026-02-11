package io.github.lycosmic.lithe.presentation.settings.reader

import io.github.lycosmic.data.settings.AppChapterTitleAlign
import io.github.lycosmic.data.settings.AppImageAlign
import io.github.lycosmic.data.settings.AppPageAnim
import io.github.lycosmic.data.settings.AppTextAlign
import io.github.lycosmic.data.settings.ImageColorEffect
import io.github.lycosmic.data.settings.ProgressRecord
import io.github.lycosmic.data.settings.ProgressTextAlign
import io.github.lycosmic.data.settings.ReaderFontWeight
import io.github.lycosmic.data.settings.ScreenOrientation


sealed class ReaderSettingsEvent {
    /**
     * 字体 ID 改变
     */
    data class OnFontIdChange(val fontId: String) : ReaderSettingsEvent()

    /**
     * 字体大小改变
     */
    data class OnFontSizeChange(val fontSize: Int) : ReaderSettingsEvent()

    /**
     * 字体粗细改变
     */
    data class OnFontWeightChange(val fontWeight: ReaderFontWeight) : ReaderSettingsEvent()

    /**
     * 字体斜体改变
     */
    data class OnItalicChange(val isItalic: Boolean) : ReaderSettingsEvent()

    /**
     * 字体间距改变
     */
    data class OnLetterSpacingChange(val letterSpacing: Int) : ReaderSettingsEvent()

    /**
     * 文本对齐方式改变
     */
    data class OnTextAlignChange(val textAlign: AppTextAlign) : ReaderSettingsEvent()

    /**
     * 行高改变
     */
    data class OnLineHeightChange(val lineHeight: Int) : ReaderSettingsEvent()

    /**
     * 段间距改变
     */
    data class OnParagraphSpacingChange(val paragraphSpacing: Int) : ReaderSettingsEvent()

    /**
     * 段首缩进改变
     */
    data class OnParagraphIndentChange(val paragraphIndent: Int) : ReaderSettingsEvent()

    /**
     * 图片可见性改变
     */
    data class OnImageVisibleChange(val isVisible: Boolean) : ReaderSettingsEvent()

    /**
     * 图片说明可见性改变
     */
    data class OnImageCaptionVisibleChange(val isVisible: Boolean) : ReaderSettingsEvent()

    /**
     * 图片颜色效果改变
     */
    data class OnImageColorEffectChange(val effect: ImageColorEffect) : ReaderSettingsEvent()

    /**
     * 图片圆角半径改变
     */
    data class OnImageCornerRadiusChange(val radius: Int) : ReaderSettingsEvent()

    /**
     * 图片对齐方式改变
     */
    data class OnImageAlignChange(val align: AppImageAlign) : ReaderSettingsEvent()

    /**
     * 图片大小比例改变
     */
    data class OnImageSizePercentChange(val percent: Float) : ReaderSettingsEvent()

    /**
     * 章节标题对齐方式改变
     */
    data class OnChapterTitleAlignChange(val align: AppChapterTitleAlign) : ReaderSettingsEvent()

    /**
     * 翻页模式改变
     */
    data class OnPageTurnModeChange(val mode: AppPageAnim) : ReaderSettingsEvent()

    /**
     * 侧边填充改变
     */
    data class OnSidePaddingChange(val padding: Int) : ReaderSettingsEvent()

    /**
     * 垂直填充改变
     */
    data class OnVerticalPaddingChange(val padding: Int) : ReaderSettingsEvent()

    /**
     * 刘海边距应用改变
     */
    data class OnCutoutPaddingApplyChange(val isApply: Boolean) : ReaderSettingsEvent()

    /**
     * 底部边距改变
     */
    data class OnBottomMarginChange(val margin: Int) : ReaderSettingsEvent()

    /**
     * 自定义亮度启用改变
     */
    data class OnCustomBrightnessEnabledChange(val isEnabled: Boolean) : ReaderSettingsEvent()

    /**
     * 自定义亮度值改变
     */
    data class OnCustomBrightnessValueChange(val value: Float) : ReaderSettingsEvent()

    /**
     * 屏幕方向改变
     */
    data class OnScreenOrientationChange(val orientation: ScreenOrientation) : ReaderSettingsEvent()

    /**
     * 进度记录模式改变
     */
    data class OnProgressRecordModeChange(val mode: ProgressRecord) : ReaderSettingsEvent()

    /**
     * 进度条可见性改变
     */
    data class OnProgressBarVisibleChange(val isVisible: Boolean) : ReaderSettingsEvent()

    /**
     * 全屏模式改变
     */
    data class OnIsFullScreenChange(val isFullScreen: Boolean) : ReaderSettingsEvent()

    /**
     * 屏幕常亮改变
     */
    data class OnIsKeepScreenOnChange(val isKeep: Boolean) : ReaderSettingsEvent()

    /**
     * 快速滚动时隐藏栏改变
     */
    data class OnIsHideBarWhenQuickScrollChange(val isHide: Boolean) : ReaderSettingsEvent()

    /**
     * 进度条字体大小改变
     */
    data class OnProgressBarFontSizeChange(val size: Int) : ReaderSettingsEvent()

    /**
     * 进度条边距改变
     */
    data class OnProgressBarMarginChange(val margin: Int) : ReaderSettingsEvent()

    /**
     * 进度条文本对齐改变
     */
    data class OnProgressBarTextAlignChange(val align: ProgressTextAlign) : ReaderSettingsEvent()
}