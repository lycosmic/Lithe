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
            currentFontId = currentFontId,
            currentFontFamily = currentFontFamily,
            currentFontSize = currentFontSize,
            currentFontWeight = currentFontWeight,
            isReaderItalic = isReaderItalic,
            currentLetterSpacing = currentLetterSpacing,
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
                )
        )
    }
}