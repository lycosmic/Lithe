package io.github.lycosmic.lithe.ui.theme.color

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Light
private val primaryLight = Color(0xFF824C76)
private val onPrimaryLight = Color(0xFFFFFFFF)
private val primaryContainerLight = Color(0xFFFFD7F1)
private val onPrimaryContainerLight = Color(0xFF35082F)
private val secondaryLight = Color(0xFF6F5867)
private val onSecondaryLight = Color(0xFFFFFFFF)
private val secondaryContainerLight = Color(0xFFF9DAED)
private val onSecondaryContainerLight = Color(0xFF271623)
private val tertiaryLight = Color(0xFF815342)
private val onTertiaryLight = Color(0xFFFFFFFF)
private val tertiaryContainerLight = Color(0xFFFFDBCF)
private val onTertiaryContainerLight = Color(0xFF321206)
private val errorLight = Color(0xFFBA1A1A)
private val onErrorLight = Color(0xFFFFFFFF)
private val errorContainerLight = Color(0xFFFFDAD6)
private val onErrorContainerLight = Color(0xFF410002)
private val backgroundLight = Color(0xFFFFF7F9)
private val onBackgroundLight = Color(0xFF201A1E)
private val surfaceLight = Color(0xFFFFF7F9)
private val onSurfaceLight = Color(0xFF201A1E)
private val surfaceVariantLight = Color(0xFFEFDEE6)
private val onSurfaceVariantLight = Color(0xFF4E444A)
private val outlineLight = Color(0xFF80747B)
private val outlineVariantLight = Color(0xFFD2C2CA)
private val scrimLight = Color(0xFF000000)
private val inverseSurfaceLight = Color(0xFF362E33)
private val inverseOnSurfaceLight = Color(0xFFFBEDF3)
private val inversePrimaryLight = Color(0xFFF4B2E2)
private val surfaceDimLight = Color(0xFFE3D7DC)
private val surfaceBrightLight = Color(0xFFFFF7F9)
private val surfaceContainerLowestLight = Color(0xFFFFFFFF)
private val surfaceContainerLowLight = Color(0xFFFEF0F6)
private val surfaceContainerLight = Color(0xFFF8EAF0)
private val surfaceContainerHighLight = Color(0xFFF2E5EB)
private val surfaceContainerHighestLight = Color(0xFFECDFE5)

// Dark
private val primaryDark = Color(0xFFF4B2E2)
private val onPrimaryDark = Color(0xFF4E1E46)
private val primaryContainerDark = Color(0xFF67355D)
private val onPrimaryContainerDark = Color(0xFFFFD7F1)
private val secondaryDark = Color(0xFFDCBED1)
private val onSecondaryDark = Color(0xFF3E2A39)
private val secondaryContainerDark = Color(0xFF56404F)
private val onSecondaryContainerDark = Color(0xFFF9DAED)
private val tertiaryDark = Color(0xFFF5B9A4)
private val onTertiaryDark = Color(0xFF4C2618)
private val tertiaryContainerDark = Color(0xFF663C2C)
private val onTertiaryContainerDark = Color(0xFFFFDBCF)
private val errorDark = Color(0xFFFFB4AB)
private val onErrorDark = Color(0xFF690005)
private val errorContainerDark = Color(0xFF93000A)
private val onErrorContainerDark = Color(0xFFFFDAD6)
private val backgroundDark = Color(0xFF181216)
private val onBackgroundDark = Color(0xFFECDFE5)
private val surfaceDark = Color(0xFF181216)
private val onSurfaceDark = Color(0xFFECDFE5)
private val surfaceVariantDark = Color(0xFF4E444A)
private val onSurfaceVariantDark = Color(0xFFD2C2CA)
private val outlineDark = Color(0xFF9B8D95)
private val outlineVariantDark = Color(0xFF4E444A)
private val scrimDark = Color(0xFF000000)
private val inverseSurfaceDark = Color(0xFFECDFE5)
private val inverseOnSurfaceDark = Color(0xFF362E33)
private val inversePrimaryDark = Color(0xFF824C76)
private val surfaceDimDark = Color(0xFF181216)
private val surfaceBrightDark = Color(0xFF3F373C)
private val surfaceContainerLowestDark = Color(0xFF120C10)
private val surfaceContainerLowDark = Color(0xFF201A1E)
private val surfaceContainerDark = Color(0xFF241E22)
private val surfaceContainerHighDark = Color(0xFF2F282C)
private val surfaceContainerHighestDark = Color(0xFF3A3337)

@Composable
fun erisColorScheme(isDark: Boolean): ColorScheme {
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