package io.github.lycosmic.lithe.presentation.settings.reader

import io.github.lycosmic.data.settings.AppImageAlign
import io.github.lycosmic.data.settings.AppTextAlign
import io.github.lycosmic.data.settings.ImageColorEffect


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
    data class OnFontWeightChange(val fontWeight: Int) : ReaderSettingsEvent()

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
}