package io.github.lycosmic.domain.model

data class Book(
    val id: Long = 0,
    val uniqueId: String,
    val title: String,
    val author: List<String>,
    val description: String?,
    val fileSize: Long,
    val fileUri: String,
    val format: FileFormat,
    val coverPath: String?,
    val importTime: Long,
    val progress: Float = 0f,
    val lastReadTime: Long? = null,
    val categories: List<Category> = emptyList() // 书籍的分类，默认为空
) {
    companion object {
        val Empty = Book(
            id = 0,
            uniqueId = "",
            title = "",
            author = emptyList(),
            description = null,
            fileSize = 0,
            fileUri = "",
            format = FileFormat.UNKNOWN,
            coverPath = null,
            importTime = 0,
            progress = 0f,
            lastReadTime = null
        )
    }
}