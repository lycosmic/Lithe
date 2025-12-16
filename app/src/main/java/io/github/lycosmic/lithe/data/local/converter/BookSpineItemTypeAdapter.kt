package io.github.lycosmic.lithe.data.local.converter

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonParseException
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import io.github.lycosmic.lithe.data.model.BookSpineItem
import io.github.lycosmic.lithe.data.model.EpubSpineItem
import io.github.lycosmic.lithe.data.model.EpubSpineItem.Companion.CONTENT_HREF_MEMBER
import io.github.lycosmic.lithe.data.model.TxtSpineItem
import java.lang.reflect.Type

class BookSpineItemTypeAdapter : JsonDeserializer<BookSpineItem>, JsonSerializer<BookSpineItem> {

    /**
     * 反序列化
     */
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): BookSpineItem {
        if (json == null || !json.isJsonObject || context == null) {
            throw JsonParseException("Invalid JSON for BookSpineItem")
        }

        val jsonObject = json.asJsonObject
        return when {
            // 根据是否有 contentHref 字段来判断是 EPUB 还是 TXT
            jsonObject.has(CONTENT_HREF_MEMBER) -> {
                context.deserialize(jsonObject, EpubSpineItem::class.java)
            }

            else -> {
                context.deserialize(jsonObject, TxtSpineItem::class.java)
            }
        }
    }

    /**
     * 序列化
     */
    override fun serialize(
        src: BookSpineItem?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        if (src == null || context == null) {
            return JsonNull.INSTANCE
        }

        return when (src) {
            is EpubSpineItem -> context.serialize(src, EpubSpineItem::class.java)
            is TxtSpineItem -> context.serialize(src, TxtSpineItem::class.java)
        }
    }
}
