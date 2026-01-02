package io.github.lycosmic.lithe.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import io.github.lycosmic.lithe.ui.theme.color.callistoColorScheme
import io.github.lycosmic.lithe.ui.theme.color.ceresColorScheme
import io.github.lycosmic.lithe.ui.theme.color.earthColorScheme
import io.github.lycosmic.lithe.ui.theme.color.enceladusColorScheme
import io.github.lycosmic.lithe.ui.theme.color.erisColorScheme
import io.github.lycosmic.lithe.ui.theme.color.ganymedeColorScheme
import io.github.lycosmic.lithe.ui.theme.color.iapetusColorScheme
import io.github.lycosmic.lithe.ui.theme.color.ioColorScheme
import io.github.lycosmic.lithe.ui.theme.color.jupiterColorScheme
import io.github.lycosmic.lithe.ui.theme.color.makemakeColorScheme
import io.github.lycosmic.lithe.ui.theme.color.marsColorScheme
import io.github.lycosmic.lithe.ui.theme.color.mercuryColorScheme
import io.github.lycosmic.lithe.ui.theme.color.neptuneColorScheme
import io.github.lycosmic.lithe.ui.theme.color.plutoColorScheme
import io.github.lycosmic.lithe.ui.theme.color.saturnColorScheme
import io.github.lycosmic.lithe.ui.theme.color.uranusColorScheme
import io.github.lycosmic.lithe.ui.theme.color.venusColorScheme
import io.github.lycosmic.model.AppThemeOption


@Composable
fun colorScheme(
    appTheme: AppThemeOption,
    isDark: Boolean = isSystemInDarkTheme()
): ColorScheme {
    val colorScheme = when (appTheme) {
        AppThemeOption.MERCURY -> {
            mercuryColorScheme(isDark)
        }

        AppThemeOption.NEPTUNE -> {
            neptuneColorScheme(isDark)
        }

        AppThemeOption.EARTH -> {
            earthColorScheme(isDark)
        }

        AppThemeOption.IO -> {
            ioColorScheme(isDark)
        }

        AppThemeOption.ENCELADUS -> {
            enceladusColorScheme(isDark)
        }

        AppThemeOption.PLUTO -> {
            plutoColorScheme(isDark)
        }

        AppThemeOption.MARS -> {
            marsColorScheme(isDark)
        }

        AppThemeOption.CALLISTO -> {
            callistoColorScheme(isDark)
        }

        AppThemeOption.JUPITER -> {
            jupiterColorScheme(isDark)
        }

        AppThemeOption.CERES -> {
            ceresColorScheme(isDark)
        }

        AppThemeOption.ERIS -> {
            erisColorScheme(isDark)
        }

        AppThemeOption.SATURN -> {
            saturnColorScheme(isDark)
        }

        AppThemeOption.GANYMEDE -> {
            ganymedeColorScheme(isDark)
        }

        AppThemeOption.VENUS -> {
            venusColorScheme(isDark)
        }

        AppThemeOption.MAKEMAKE -> {
            makemakeColorScheme(isDark)
        }

        AppThemeOption.URANUS -> {
            uranusColorScheme(isDark)
        }

        AppThemeOption.IAPETUS -> {
            iapetusColorScheme(isDark)
        }
    }

    return colorScheme
}