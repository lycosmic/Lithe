package io.github.lycosmic.data.settings

/**
 * 阅读器图片对齐方式
 */
enum class AppImageAlign {
    START,       // 左对齐/起始
    CENTER,      // 居中
    END;         // 右对齐/末尾

    companion object {
        fun fromName(name: String?, default: AppImageAlign = CENTER): AppImageAlign {
            return entries.find { it.name == name } ?: default
        }
    }
}