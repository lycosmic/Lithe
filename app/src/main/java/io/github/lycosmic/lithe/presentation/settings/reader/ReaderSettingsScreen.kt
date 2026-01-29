package io.github.lycosmic.lithe.presentation.settings.reader

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


/**
 * 阅读器设置页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderSettingsScreen(onNavigateBack: () -> Unit, modifier: Modifier = Modifier) {

    val topBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            ReaderSettingsTopBar(
                onBackClick = onNavigateBack,
                topBarScrollBehavior = topBarScrollBehavior
            )
        },
    ) { innerPaddings ->
        ReaderSettingsContent(
            modifier = modifier.padding(
                top = innerPaddings.calculateTopPadding(),
                bottom = innerPaddings.calculateBottomPadding()
            )
        )
    }
}