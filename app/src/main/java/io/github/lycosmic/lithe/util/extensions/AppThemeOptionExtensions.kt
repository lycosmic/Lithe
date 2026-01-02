package io.github.lycosmic.lithe.util.extensions

import io.github.lycosmic.lithe.R
import io.github.lycosmic.model.AppThemeOption

/**
 * 获取应用主题的标签资源 ID
 */
val AppThemeOption.labelResId: Int
    get() = when (this) {
        AppThemeOption.MERCURY -> R.string.mercury
        AppThemeOption.NEPTUNE -> R.string.neptune
        AppThemeOption.EARTH -> R.string.earth
        AppThemeOption.IO -> R.string.io
        AppThemeOption.ENCELADUS -> R.string.enceladus
        AppThemeOption.PLUTO -> R.string.pluto
        AppThemeOption.MARS -> R.string.mars
        AppThemeOption.CALLISTO -> R.string.callisto
        AppThemeOption.JUPITER -> R.string.jupiter
        AppThemeOption.CERES -> R.string.ceres
        AppThemeOption.ERIS -> R.string.eris
        AppThemeOption.SATURN -> R.string.saturn
        AppThemeOption.GANYMEDE -> R.string.ganymede
        AppThemeOption.VENUS -> R.string.venus
        AppThemeOption.MAKEMAKE -> R.string.make_make
        AppThemeOption.URANUS -> R.string.uranus
        AppThemeOption.IAPETUS -> R.string.iapetus
    }