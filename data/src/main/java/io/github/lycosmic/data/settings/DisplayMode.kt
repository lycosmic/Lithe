package io.github.lycosmic.data.settings

/**
 * 显示模式 - 用于书库中书籍和浏览器文件夹的显示模式
 */
enum class DisplayMode {
    List,
    Grid;

    companion object {
        fun fromName(name: String?, default: DisplayMode = List): DisplayMode {
            return entries.find { it.name == name } ?: default
        }
    }
}