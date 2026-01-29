package io.github.lycosmic.lithe.presentation.settings.reader

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


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
        Box(
            Modifier
                .fillMaxSize()
                .padding(innerPaddings)
        ) {
            // TODO: 添加阅读设置页面
            Text(text = "阅读器页面")
        }
    }
}