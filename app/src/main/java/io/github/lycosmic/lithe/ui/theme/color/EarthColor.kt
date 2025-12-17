package io.github.lycosmic.lithe.ui.theme.color

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Light
private val primaryLight = Color(0xFF306A42)
private val onPrimaryLight = Color(0xFFFFFFFF)
private val primaryContainerLight = Color(0xFFB3F1BF)
private val onPrimaryContainerLight = Color(0xFF00210D)
private val secondaryLight = Color(0xFF506353)
private val onSecondaryLight = Color(0xFFFFFFFF)
private val secondaryContainerLight = Color(0xFFD2E8D3)
private val onSecondaryContainerLight = Color(0xFF0D1F12)
private val tertiaryLight = Color(0xFF3A656F)
private val onTertiaryLight = Color(0xFFFFFFFF)
private val tertiaryContainerLight = Color(0xFFBEEAF6)
private val onTertiaryContainerLight = Color(0xFF001F25)
private val errorLight = Color(0xFFBA1A1A)
private val onErrorLight = Color(0xFFFFFFFF)
private val errorContainerLight = Color(0xFFFFDAD6)
private val onErrorContainerLight = Color(0xFF410002)
private val backgroundLight = Color(0xFFF6FBF3)
private val onBackgroundLight = Color(0xFF181D18)
private val surfaceLight = Color(0xFFF6FBF3)
private val onSurfaceLight = Color(0xFF181D18)
private val surfaceVariantLight = Color(0xFFDDE5DA)
private val onSurfaceVariantLight = Color(0xFF414941)
private val outlineLight = Color(0xFF717971)
private val outlineVariantLight = Color(0xFFC1C9BF)
private val scrimLight = Color(0xFF000000)
private val inverseSurfaceLight = Color(0xFF2D322D)
private val inverseOnSurfaceLight = Color(0xFFEEF2EA)
private val inversePrimaryLight = Color(0xFF97D5A5)
private val surfaceDimLight = Color(0xFFD7DBD4)
private val surfaceBrightLight = Color(0xFFF6FBF3)
private val surfaceContainerLowestLight = Color(0xFFFFFFFF)
private val surfaceContainerLowLight = Color(0xFFF0F5ED)
private val surfaceContainerLight = Color(0xFFEBEFE7)
private val surfaceContainerHighLight = Color(0xFFE5EAE2)
private val surfaceContainerHighestLight = Color(0xFFDFE4DC)

// Dark
private val primaryDark = Color(0xFF97D5A5)
private val onPrimaryDark = Color(0xFF00391A)
private val primaryContainerDark = Color(0xFF15512D)
private val onPrimaryContainerDark = Color(0xFFB3F1BF)
private val secondaryDark = Color(0xFFB6CCB8)
private val onSecondaryDark = Color(0xFF223526)
private val secondaryContainerDark = Color(0xFF384B3C)
private val onSecondaryContainerDark = Color(0xFFD2E8D3)
private val tertiaryDark = Color(0xFFA2CED9)
private val onTertiaryDark = Color(0xFF01363F)
private val tertiaryContainerDark = Color(0xFF204D56)
private val onTertiaryContainerDark = Color(0xFFBEEAF6)
private val errorDark = Color(0xFFFFB4AB)
private val onErrorDark = Color(0xFF690005)
private val errorContainerDark = Color(0xFF93000A)
private val onErrorContainerDark = Color(0xFFFFDAD6)
private val backgroundDark = Color(0xFF101510)
private val onBackgroundDark = Color(0xFFDFE4DC)
private val surfaceDark = Color(0xFF101510)
private val onSurfaceDark = Color(0xFFDFE4DC)
private val surfaceVariantDark = Color(0xFF414941)
private val onSurfaceVariantDark = Color(0xFFC1C9BF)
private val outlineDark = Color(0xFF8B938A)
private val outlineVariantDark = Color(0xFF414941)
private val scrimDark = Color(0xFF000000)
private val inverseSurfaceDark = Color(0xFFDFE4DC)
private val inverseOnSurfaceDark = Color(0xFF2D322D)
private val inversePrimaryDark = Color(0xFF306A42)
private val surfaceDimDark = Color(0xFF101510)
private val surfaceBrightDark = Color(0xFF353A35)
private val surfaceContainerLowestDark = Color(0xFF0A0F0B)
private val surfaceContainerLowDark = Color(0xFF181D18)
private val surfaceContainerDark = Color(0xFF1C211C)
private val surfaceContainerHighDark = Color(0xFF262B26)
private val surfaceContainerHighestDark = Color(0xFF313631)


@Composable
fun earthColorScheme(isDark: Boolean): ColorScheme {
    return if (isDark) {
        darkColorScheme(
            primary = primaryDark,
            onPrimary = onPrimaryDark,
            primaryContainer = primaryContainerDark,
            onPrimaryContainer = onPrimaryContainerDark,
            secondary = secondaryDark,
            onSecondary = onSecondaryDark,
            secondaryContainer = secondaryContainerDark,
            onSecondaryContainer = onSecondaryContainerDark,
            tertiary = tertiaryDark,
            onTertiary = onTertiaryDark,
            tertiaryContainer = tertiaryContainerDark,
            onTertiaryContainer = onTertiaryContainerDark,
            error = errorDark,
            onError = onErrorDark,
            errorContainer = errorContainerDark,
            onErrorContainer = onErrorContainerDark,
            background = backgroundDark,
            onBackground = onBackgroundDark,
            surface = surfaceDark,
            onSurface = onSurfaceDark,
            surfaceVariant = surfaceVariantDark,
            onSurfaceVariant = onSurfaceVariantDark,
            outline = outlineDark,
            outlineVariant = outlineVariantDark,
            scrim = scrimDark,
            inverseSurface = inverseSurfaceDark,
            inverseOnSurface = inverseOnSurfaceDark,
            inversePrimary = inversePrimaryDark,
            surfaceDim = surfaceDimDark,
            surfaceBright = surfaceBrightDark,
            surfaceContainerLowest = surfaceContainerLowestDark,
            surfaceContainerLow = surfaceContainerLowDark,
            surfaceContainer = surfaceContainerDark,
            surfaceContainerHigh = surfaceContainerHighDark,
            surfaceContainerHighest = surfaceContainerHighestDark,
        )
    } else {
        lightColorScheme(
            primary = primaryLight,
            onPrimary = onPrimaryLight,
            primaryContainer = primaryContainerLight,
            onPrimaryContainer = onPrimaryContainerLight,
            secondary = secondaryLight,
            onSecondary = onSecondaryLight,
            secondaryContainer = secondaryContainerLight,
            onSecondaryContainer = onSecondaryContainerLight,
            tertiary = tertiaryLight,
            onTertiary = onTertiaryLight,
            tertiaryContainer = tertiaryContainerLight,
            onTertiaryContainer = onTertiaryContainerLight,
            error = errorLight,
            onError = onErrorLight,
            errorContainer = errorContainerLight,
            onErrorContainer = onErrorContainerLight,
            background = backgroundLight,
            onBackground = onBackgroundLight,
            surface = surfaceLight,
            onSurface = onSurfaceLight,
            surfaceVariant = surfaceVariantLight,
            onSurfaceVariant = onSurfaceVariantLight,
            outline = outlineLight,
            outlineVariant = outlineVariantLight,
            scrim = scrimLight,
            inverseSurface = inverseSurfaceLight,
            inverseOnSurface = inverseOnSurfaceLight,
            inversePrimary = inversePrimaryLight,
            surfaceDim = surfaceDimLight,
            surfaceBright = surfaceBrightLight,
            surfaceContainerLowest = surfaceContainerLowestLight,
            surfaceContainerLow = surfaceContainerLowLight,
            surfaceContainer = surfaceContainerLight,
            surfaceContainerHigh = surfaceContainerHighLight,
            surfaceContainerHighest = surfaceContainerHighestLight,
        )
    }
}