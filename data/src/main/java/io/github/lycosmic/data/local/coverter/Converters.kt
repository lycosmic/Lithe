package io.github.lycosmic.data.local.coverter

import android.util.Log
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    private val gson = Gson()

    companion object {
        private const val TAG = "Converters"
    }

    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        return gson.toJson(list ?: emptyList<String>())
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value == null) return emptyList()
        val listType = object : TypeToken<List<String>>() {}.type
        return try {
            gson.fromJson(value, listType)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to convert to StringList: $value", e)
            emptyList()
        }
    }
}