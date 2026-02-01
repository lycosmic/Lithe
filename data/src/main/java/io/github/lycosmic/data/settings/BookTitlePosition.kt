package io.github.lycosmic.data.settings

/**
 * 书籍标题的位置
 */
enum class BookTitlePosition() {
    Hidden,  // 隐藏
    Below,   // 下方
    Inside;   // 内部

    companion object {
        fun fromName(name: String?, default: BookTitlePosition = Below): BookTitlePosition {
            return entries.find { it.name == name } ?: default
        }
    }
}