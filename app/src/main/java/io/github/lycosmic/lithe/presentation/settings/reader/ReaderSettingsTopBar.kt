package io.github.lycosmic.lithe.presentation.settings.reader

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.github.lycosmic.lithe.R


/**
 * 阅读器设置页面的顶部栏
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderSettingsTopBar(
    onBackClick: () -> Unit,
    topBarScrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    LargeTopAppBar(
        title = {
            Text(text = stringResource(id = R.string.reader_settings_title))
        },
        modifier = modifier,
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
            }
        },
        scrollBehavior = topBarScrollBehavior
    )
}


