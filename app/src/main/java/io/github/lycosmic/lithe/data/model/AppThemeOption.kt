package io.github.lycosmic.lithe.data.model

import androidx.annotation.StringRes
import io.github.lycosmic.lithe.R


enum class AppThemeOption(
    val id: String,
    @field:StringRes
    val labelResId: Int, // 显示的标题资源 ID
) {
    // 默认的水星主题，也是唯一的动态主题
    MERCURY(
        id = "mercury",
        labelResId = R.string.mercury,
    ),

    // 海王星
    NEPTUNE(
        id = "neptune",
        labelResId = R.string.neptune,
    ),

    // 地球
    EARTH(
        id = "earth",
        labelResId = R.string.earth,
    ),

    // 木卫一
    IO(
        id = "io",
        labelResId = R.string.io
    ),

    // 土卫二
    ENCELADUS(
        id = "enceladus",
        labelResId = R.string.enceladus,
    ),

    // 冥王星
    PLUTO(
        id = "pluto",
        labelResId = R.string.pluto,
    ),

    // 火星
    MARS(
        id = "mars",
        labelResId = R.string.mars,
    ),

    // 木卫四
    CALLISTO(
        id = "callisto",
        labelResId = R.string.callisto,
    ),

    // 木星
    JUPITER(
        id = "jupiter",
        labelResId = R.string.jupiter,
    ),

    // 谷神星
    CERES(
        id = "ceres",
        labelResId = R.string.ceres,
    ),

    // 阋神星
    ERIS(
        id = "eris",
        labelResId = R.string.eris,
    ),

    // 土星
    SATURN(
        id = "saturn",
        labelResId = R.string.saturn,
    ),

    // 木卫三
    GANYMEDE(
        id = "ganymede",
        labelResId = R.string.ganymede,
    ),

    // 金星
    VENUS(
        id = "venus",
        labelResId = R.string.venus,
    ),

    // 鸟神星
    MAKEMAKE(
        id = "makemake",
        labelResId = R.string.makemake,
    ),

    // 天王星
    URANUS(
        id = "uranus",
        labelResId = R.string.uranus,
    ),

    // 土卫八
    IAPETUS(
        id = "iapetus",
        labelResId = R.string.iapetus,
    );

    companion object {
        fun fromId(id: String): AppThemeOption {
            return entries.firstOrNull { it.id == id }
                ?: throw IllegalArgumentException("无效的应用主题 ID: $id")
        }
    }
}