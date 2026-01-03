package io.github.lycosmic.domain.model

/**
 * OPF 文件解析结果
 */
data class OpfParseResult(
    val uniqueIdentifier: String? = null,
    val title: String? = null,
    val authors: List<String> = emptyList(),
    val language: String? = null,
    val description: String? = null,
    val publisher: String? = null,
    val subjects: List<String> = emptyList(), // 标签
    val coverHref: String?, // 封面相对路径
    val manifest: Map<String, ManifestItem> = emptyMap(), // 资源清单: id -> item
    val spine: List<String> // 阅读顺序, 存放 id
) {
    companion object {
        const val PACKAGE = "package"
        const val UNIQUE_IDENTIFIER = "unique-identifier"
        const val TITLE = "title"
        const val CREATOR = "creator"
        const val IDENTIFIER = "identifier"
        const val LANGUAGE = "language"
        const val DESCRIPTION = "description"
        const val PUBLISHER = "publisher"
        const val SUBJECT = "subject"
        const val META = "meta"
        const val COVER = "cover"
        const val ITEM_REF = "itemref"
        const val NAME = "name"
        const val ITEM = "item"
        const val ID = "id"
        val COVER_IMAGE_LIST = listOf("coverimage", "cover_image", "cover-image")
        const val ID_REF = "idref"
        const val HREF = "href"
        const val MEDIA_TYPE = "media-type"
    }

}