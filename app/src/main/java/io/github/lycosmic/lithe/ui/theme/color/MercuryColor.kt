package io.github.lycosmic.lithe.ui.theme.color

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Mercury Light
private val primaryLight = Color(0xFF526070)
private val onPrimaryLight = Color(0xFFFFFFFF)
private val primaryContainerLight = Color(0xFFD5E4F7)
private val onPrimaryContainerLight = Color(0xFF0E1D2A)
private val secondaryLight = Color(0xFF595E66)
private val onSecondaryLight = Color(0xFFFFFFFF)
private val secondaryContainerLight = Color(0xFFDDE2EA)
private val onSecondaryContainerLight = Color(0xFF161C22)
private val tertiaryLight = Color(0xFF655B71)
private val onTertiaryLight = Color(0xFFFFFFFF)
private val tertiaryContainerLight = Color(0xFFECDEFA)
private val onTertiaryContainerLight = Color(0xFF20182A)
private val errorLight = Color(0xFFBA1A1A)
private val onErrorLight = Color(0xFFFFFFFF)
private val errorContainerLight = Color(0xFFFFDAD6)
private val onErrorContainerLight = Color(0xFF410002)
private val backgroundLight = Color(0xFFF9F9FB)
private val onBackgroundLight = Color(0xFF1A1C1E)
private val surfaceLight = Color(0xFFF9F9FB)
private val onSurfaceLight = Color(0xFF1A1C1E)
private val surfaceVariantLight = Color(0xFFE0E2E9)
private val onSurfaceVariantLight = Color(0xFF44474D)
private val outlineLight = Color(0xFF74777D)
private val outlineVariantLight = Color(0xFFC4C7CE)
private val scrimLight = Color(0xFF000000)
private val inverseSurfaceLight = Color(0xFF2F3033)
private val inverseOnSurfaceLight = Color(0xFFF1F0F4)
private val inversePrimaryLight = Color(0xFFBAC8DA)
private val surfaceDimLight = Color(0xFFD9D9DD)
private val surfaceBrightLight = Color(0xFFF9F9FB)
private val surfaceContainerLowestLight = Color(0xFFFFFFFF)
private val surfaceContainerLowLight = Color(0xFFF3F3F7)
private val surfaceContainerLight = Color(0xFFEEEEF1)
private val surfaceContainerHighLight = Color(0xFFE8E8EB)
private val surfaceContainerHighestLight = Color(0xFFE2E2E6)

// Mercury Dark
private val primaryDark = Color(0xFFBAC8DA)
private val onPrimaryDark = Color(0xFF243240)
private val primaryContainerDark = Color(0xFF3B4857)
private val onPrimaryContainerDark = Color(0xFFD5E4F7)
private val secondaryDark = Color(0xFFC1C7CE)
private val onSecondaryDark = Color(0xFF2B3037)
private val secondaryContainerDark = Color(0xFF41474E)
private val onSecondaryContainerDark = Color(0xFFDDE2EA)
private val tertiaryDark = Color(0xFFD0C2DE)
private val onTertiaryDark = Color(0xFF362D41)
private val tertiaryContainerDark = Color(0xFF4D4359)
private val onTertiaryContainerDark = Color(0xFFECDEFA)
private val errorDark = Color(0xFFFFB4AB)
private val onErrorDark = Color(0xFF690005)
private val errorContainerDark = Color(0xFF93000A)
private val onErrorContainerDark = Color(0xFFFFDAD6)
private val backgroundDark = Color(0xFF111315) // 深邃的真空黑
private val onBackgroundDark = Color(0xFFE2E2E6)
private val surfaceDark = Color(0xFF111315)
private val onSurfaceDark = Color(0xFFE2E2E6)
private val surfaceVariantDark = Color(0xFF44474D)
private val onSurfaceVariantDark = Color(0xFFC4C7CE)
private val outlineDark = Color(0xFF8E9199)
private val outlineVariantDark = Color(0xFF44474D)
private val scrimDark = Color(0xFF000000)
private val inverseSurfaceDark = Color(0xFFE2E2E6)
private val inverseOnSurfaceDark = Color(0xFF2F3033)
private val inversePrimaryDark = Color(0xFF526070)
private val surfaceDimDark = Color(0xFF111315)
private val surfaceBrightDark = Color(0xFF37393B)
private val surfaceContainerLowestDark = Color(0xFF0C0E10)
private val surfaceContainerLowDark = Color(0xFF1A1C1E)
private val surfaceContainerDark = Color(0xFF1E2022)
private val surfaceContainerHighDark = Color(0xFF282A2D)
private val surfaceContainerHighestDark = Color(0xFF333538)


private val DarkColorScheme = darkColorScheme(
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

private val LightColorScheme = lightColorScheme(
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

/**
 * 水星主题是动态主题
 */
@Composable
fun mercuryColorScheme(isDark: Boolean): ColorScheme {
    val context = LocalContext.current
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (isDark) {
            dynamicDarkColorScheme(context)
        } else {
            dynamicLightColorScheme(context)
        }
    } else {
        if (isDark) {
            DarkColorScheme
        } else {
            LightColorScheme
        }
    }
}