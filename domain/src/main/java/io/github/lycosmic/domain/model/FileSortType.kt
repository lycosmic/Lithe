package io.github.lycosmic.domain.model

/**
 * 排序类型
 */
enum class FileSortType {
    // 字母顺序
    ALPHABETICAL,

    // 文件格式
    FILE_FORMAT,

    // 最后修改
    LAST_MODIFIED,

    // 文件大小
    SIZE;

    companion object {
        // 默认排序类型为最后修改
        val DEFAULT_SORT_TYPE = LAST_MODIFIED

        // 默认排序顺序为降序
        const val DEFAULT_IS_ASCENDING = false
    }
}