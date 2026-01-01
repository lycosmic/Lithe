package io.github.lycosmic.lithe.data.local.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import io.github.lycosmic.lithe.domain.model.BookSpineItem

class BookTypeConverters {

    private val gson: Gson = GsonBuilder()
        .registerTypeAdapter(BookSpineItem::class.java, BookSpineItemTypeAdapter())
        .create()

    @TypeConverter
    fun toBookSpineItemList(value: String?): List<BookSpineItem> {
        if (value == null) return emptyList()
        val listType = object : TypeToken<List<BookSpineItem>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromBookSpineItemList(list: List<BookSpineItem>?): String {
        return gson.toJson(list ?: emptyList<BookSpineItem>())
    }

    @TypeConverter
    fun fromStringList(value: String?): List<String> {
        if (value == null) return emptyList()
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun toStringList(list: List<String>?): String {
        return Gson().toJson(list ?: emptyList<String>())
    }
}