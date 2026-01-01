package io.github.lycosmic.lithe.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import io.github.lycosmic.lithe.domain.model.AppThemeOption


@Composable
fun LitheTheme(
    appTheme: AppThemeOption = AppThemeOption.MERCURY,
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = colorScheme(appTheme, isDarkTheme)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}