package io.github.lycosmic.lithe.presentation.settings.reader

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.lycosmic.lithe.R
import io.github.lycosmic.lithe.presentation.settings.components.SettingsGroupTitle
import io.github.lycosmic.lithe.presentation.settings.components.SettingsSubGroupTitle


/**
 * 阅读器设置页面内容
 */
@Composable
fun ReaderSettingsContent(modifier: Modifier = Modifier) {
    LazyColumn(modifier) {
        item {
            SettingsGroupTitle(
                title = {
                    stringResource(id = R.string.reader_settings_font_group_title)
                },
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
            )
        }

        item {
            FontSettingsContent()
        }
        item {
            FontWeightSettingsContent()
        }
        item {
            FontStyleSettingsContent()
        }
        item {
            FontSizeSettingsContent()
        }
    }
}

/**
 * 字体系列部分
 */
@Composable
private fun FontSettingsContent(modifier: Modifier = Modifier) {
    SettingsSubGroupTitle(
        modifier = modifier.padding(vertical = 8.dp, horizontal = 16.dp),
        title = stringResource(id = R.string.reader_settings_font_family)
    )
}


/**
 * 字体粗细部分
 */
@Composable
private fun FontWeightSettingsContent(modifier: Modifier = Modifier) {
    SettingsSubGroupTitle(
        modifier = modifier.padding(vertical = 8.dp, horizontal = 16.dp),
        title = stringResource(id = R.string.reader_settings_font_weight)
    )
}

/**
 * 字体样式部分
 */
@Composable
private fun FontStyleSettingsContent(modifier: Modifier = Modifier) {
    SettingsSubGroupTitle(
        modifier = modifier.padding(vertical = 8.dp, horizontal = 16.dp),
        title = stringResource(id = R.string.reader_settings_font_style)
    )
}

/**
 * 字体大小和字水平间距部分
 */
@Composable
private fun FontSizeSettingsContent(modifier: Modifier = Modifier) {
    Column(modifier) {
        SettingsSubGroupTitle(
            modifier = modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            title = stringResource(id = R.string.reader_settings_font_size)
        )
        SettingsSubGroupTitle(
            modifier = modifier.padding(vertical = 8.dp, horizontal = 16.dp),
            title = stringResource(id = R.string.reader_settings_font_letter_spacing)
        )
    }
}