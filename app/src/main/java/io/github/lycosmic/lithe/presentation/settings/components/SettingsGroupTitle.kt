package io.github.lycosmic.lithe.presentation.settings.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color


/**
 * 设置中的分组标题，一般为主题颜色
 */
@Composable
fun SettingsGroupTitle(
    title: @Composable () -> String,
    modifier: Modifier = Modifier,
    titleColor: @Composable () -> Color = { MaterialTheme.colorScheme.primary },
) {
    SettingsSubGroupTitle(
        title = title.invoke(),
        color = titleColor.invoke(),
        modifier = modifier
    )
}