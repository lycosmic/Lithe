package io.github.lycosmic.model


enum class AppThemeOption(
    val id: String,
) {
    // 默认的水星主题，也是唯一的动态主题
    MERCURY(
        id = "mercury",
    ),

    // 海王星
    NEPTUNE(
        id = "neptune",
    ),

    // 地球
    EARTH(
        id = "earth",
    ),

    // 木卫一
    IO(
        id = "io",
    ),

    // 土卫二
    ENCELADUS(
        id = "enceladus",
    ),

    // 冥王星
    PLUTO(
        id = "pluto",
    ),

    // 火星
    MARS(
        id = "mars",
    ),

    // 木卫四
    CALLISTO(
        id = "callisto",
    ),

    // 木星
    JUPITER(
        id = "jupiter",
    ),

    // 谷神星
    CERES(
        id = "ceres",
    ),

    // 阋神星
    ERIS(
        id = "eris",
    ),

    // 土星
    SATURN(
        id = "saturn",
    ),

    // 木卫三
    GANYMEDE(
        id = "ganymede",
    ),

    // 金星
    VENUS(
        id = "venus",
    ),

    // 鸟神星
    MAKEMAKE(
        id = "makemake",
    ),

    // 天王星
    URANUS(
        id = "uranus",
    ),

    // 土卫八
    IAPETUS(
        id = "iapetus",
    );

    companion object {
        fun fromId(id: String): AppThemeOption {
            return entries.firstOrNull { it.id == id }
                ?: throw IllegalArgumentException("无效的应用主题 ID: $id")
        }
    }
}