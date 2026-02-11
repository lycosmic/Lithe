package io.github.lycosmic.lithe.presentation.settings.reader

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle


/**
 * 阅读器设置页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReaderSettingsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {

    val topBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val currentFontId by viewModel.fontId.collectAsStateWithLifecycle()

    val currentFontSize by viewModel.fontSize.collectAsStateWithLifecycle()

    val currentFontWeight by viewModel.fontWeight.collectAsStateWithLifecycle()

    val isReaderItalic by viewModel.isItalic.collectAsStateWithLifecycle()

    val currentLetterSpacing by viewModel.letterSpacing.collectAsStateWithLifecycle()

    val fonts by viewModel.availableFonts.collectAsStateWithLifecycle()

    val currentFontFamily by viewModel.fontFamily.collectAsStateWithLifecycle()

    val currentAppTextAlign by viewModel.appTextAlign.collectAsStateWithLifecycle()

    val currentLineHeight by viewModel.lineHeight.collectAsStateWithLifecycle()

    val currentParagraphSpacing by viewModel.paragraphSpacing.collectAsStateWithLifecycle()

    val currentParagraphIndent by viewModel.paragraphIndent.collectAsStateWithLifecycle()

    val currentImageVisible by viewModel.imageVisible.collectAsStateWithLifecycle()

    val currentImageCaptionVisible by viewModel.imageCaptionVisible.collectAsStateWithLifecycle()

    val currentImageColorEffect by viewModel.imageColorEffect.collectAsStateWithLifecycle()

    val currentImageCornerRadius by viewModel.imageCornerRadius.collectAsStateWithLifecycle()

    val currentImageAlign by viewModel.imageAlign.collectAsStateWithLifecycle()

    val currentImageSizePercent by viewModel.imageSizeRatio.collectAsStateWithLifecycle()

    val chapterTitleAlign by viewModel.chapterTitleAlign.collectAsStateWithLifecycle()

    val pageTurnMode by viewModel.readerMode.collectAsStateWithLifecycle()

    val sidePadding by viewModel.sidePadding.collectAsStateWithLifecycle()

    val verticalPadding by viewModel.verticalPadding.collectAsStateWithLifecycle()

    val cutoutPaddingApply by viewModel.cutoutPaddingApply.collectAsStateWithLifecycle()

    val bottomMargin by viewModel.bottomMargin.collectAsStateWithLifecycle()

    val brightnessEnabled by viewModel.isCustomBrightness.collectAsStateWithLifecycle()

    val customBrightnessValue by viewModel.customBrightness.collectAsStateWithLifecycle()

    val screenOrientation by viewModel.screenOrientation.collectAsStateWithLifecycle()

    val progressRecordMode by viewModel.progressRecord.collectAsStateWithLifecycle()

    val bottomProgressBarVisible by viewModel.isBottomProgressVisible.collectAsStateWithLifecycle()

    val progressBarFontSize by viewModel.progressTextSize.collectAsStateWithLifecycle()

    val progressBarMargin by viewModel.bottomProgressPadding.collectAsStateWithLifecycle()

    val progressBarTextAlign by viewModel.bottomProgressTextAlign.collectAsStateWithLifecycle()

    val fullScreen by viewModel.isFullScreen.collectAsStateWithLifecycle()

    val isKeepScreenOn by viewModel.screenAlive.collectAsStateWithLifecycle()

    val isHideBarWhenQuickScroll by viewModel.isHideBarOnQuickScroll.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.nestedScroll(topBarScrollBehavior.nestedScrollConnection),
        topBar = {
            ReaderSettingsTopBar(
                onBackClick = onNavigateBack,
                topBarScrollBehavior = topBarScrollBehavior
            )
        },
    ) { innerPaddings ->
        ReaderSettingsContent(
            fonts = fonts,
            fontId = currentFontId,
            currentFontFamily = currentFontFamily,
            fontSize = currentFontSize,
            fontWeight = currentFontWeight,
            isReaderItalic = isReaderItalic,
            letterSpacing = currentLetterSpacing,
            onFontIdChange = {
                viewModel.onEvent(ReaderSettingsEvent.OnFontIdChange(it))
            },
            onFontSizeChange = {
                viewModel.onEvent(ReaderSettingsEvent.OnFontSizeChange(it))
            },
            onFontWeightChange = {
                viewModel.onEvent(ReaderSettingsEvent.OnFontWeightChange(it))
            },
            onItalicChange = {
                viewModel.onEvent(ReaderSettingsEvent.OnItalicChange(it))
            },
            onLetterSpacingChange = {
                viewModel.onEvent(ReaderSettingsEvent.OnLetterSpacingChange(it))
            },
            modifier = modifier
                .fillMaxSize()
                .padding(
                    top = innerPaddings.calculateTopPadding(),
                    bottom = innerPaddings.calculateBottomPadding()
                ),
            appTextAlign = currentAppTextAlign,
            lineHeight = currentLineHeight,
            paragraphSpacing = currentParagraphSpacing,
            paragraphIndent = currentParagraphIndent,
            imageVisible = currentImageVisible,
            imageCaptionVisible = currentImageCaptionVisible,
            imageColorEffect = currentImageColorEffect,
            imageCornerRadius = currentImageCornerRadius,
            imageAlign = currentImageAlign,
            imageSizePercent = currentImageSizePercent,
            chapterTitleAlign = chapterTitleAlign,
            pageTurnMode = pageTurnMode,
            sidePadding = sidePadding,
            verticalPadding = verticalPadding,
            cutoutPaddingApply = cutoutPaddingApply,
            bottomMargin = bottomMargin,
            customBrightnessEnabled = brightnessEnabled,
            customBrightnessValue = customBrightnessValue,
            screenOrientation = screenOrientation,
            progressRecordMode = progressRecordMode,
            progressBarVisible = bottomProgressBarVisible,
            progressBarFontSize = progressBarFontSize,
            progressBarMargin = progressBarMargin,
            progressBarTextAlign = progressBarTextAlign,
            isFullScreen = fullScreen,
            isKeepScreenOn = isKeepScreenOn,
            isHideBarWhenQuickScroll = isHideBarWhenQuickScroll,
            onTextAlignChange = {
                viewModel.onEvent(ReaderSettingsEvent.OnTextAlignChange(it))
            },
            onLineHeightChange = {
                viewModel.onEvent(ReaderSettingsEvent.OnLineHeightChange(it))
            },
            onParagraphSpacingChange = {
                viewModel.onEvent(ReaderSettingsEvent.OnParagraphSpacingChange(it))
            },
            onParagraphIndentChange = {
                viewModel.onEvent(ReaderSettingsEvent.OnParagraphIndentChange(it))
            },
            onImageColorEffectChange = {
                viewModel.onEvent(ReaderSettingsEvent.OnImageColorEffectChange(it))
            },
            onImageCornerRadiusChange = {
                viewModel.onEvent(ReaderSettingsEvent.OnImageCornerRadiusChange(it))
            },
            onImageAlignChange = {
                viewModel.onEvent(ReaderSettingsEvent.OnImageAlignChange(it))
            },
            onImageSizePercentChange = {
                viewModel.onEvent(ReaderSettingsEvent.OnImageSizePercentChange(it))
            },
            onImageVisibleChange = {
                viewModel.onEvent(ReaderSettingsEvent.OnImageVisibleChange(it))
            },
            onImageCaptionVisibleChange = {
                viewModel.onEvent(ReaderSettingsEvent.OnImageCaptionVisibleChange(it))
            },
            onChapterTitleAlignChange = {
                viewModel.onEvent(ReaderSettingsEvent.OnChapterTitleAlignChange(it))
            },
            onPageTurnModeChange = {
                viewModel.onEvent(ReaderSettingsEvent.OnPageTurnModeChange(it))
            },
            onSidePaddingChange = {
                viewModel.onEvent(ReaderSettingsEvent.OnSidePaddingChange(it))
            },
            onVerticalPaddingChange = {
                viewModel.onEvent(ReaderSettingsEvent.OnVerticalPaddingChange(it))
            },
            onCutoutPaddingApplyChange = {
                viewModel.onEvent(ReaderSettingsEvent.OnCutoutPaddingApplyChange(it))
            },
            onBottomMarginChange = {
                viewModel.onEvent(ReaderSettingsEvent.OnBottomMarginChange(it))
            },
            onCustomBrightnessEnabledChange = {
                viewModel.onEvent(ReaderSettingsEvent.OnCustomBrightnessEnabledChange(it))
            },
            onCustomBrightnessValueChange = {
                viewModel.onEvent(ReaderSettingsEvent.OnCustomBrightnessValueChange(it))
            },
            onScreenOrientationChange = {
                viewModel.onEvent(ReaderSettingsEvent.OnScreenOrientationChange(it))
            },
            onProgressRecordModeChange = {
                viewModel.onEvent(ReaderSettingsEvent.OnProgressRecordModeChange(it))
            },
            onProgressBarVisibleChange = {
                viewModel.onEvent(ReaderSettingsEvent.OnProgressBarVisibleChange(it))
            },
            onIsFullScreenChange = {
                viewModel.onEvent(ReaderSettingsEvent.OnIsFullScreenChange(it))
            },
            onIsKeepScreenOnChange = {
                viewModel.onEvent(ReaderSettingsEvent.OnIsKeepScreenOnChange(it))
            },
            onIsHideBarWhenQuickScrollChange = {
                viewModel.onEvent(ReaderSettingsEvent.OnIsHideBarWhenQuickScrollChange(it))
            },
            onProgressBarFontSizeChange = {
                viewModel.onEvent(ReaderSettingsEvent.OnProgressBarFontSizeChange(it))
            },
            onProgressBarMarginChange = {
                viewModel.onEvent(ReaderSettingsEvent.OnProgressBarMarginChange(it))
            },
            onProgressBarTextAlignChange = {
                viewModel.onEvent(ReaderSettingsEvent.OnProgressBarTextAlignChange(it))
            }
        )
    }
}