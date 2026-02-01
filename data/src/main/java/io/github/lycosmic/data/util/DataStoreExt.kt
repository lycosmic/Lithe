package io.github.lycosmic.data.util

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// ===== DataStore扩展 =====
/**
 * 读取普通值
 */
fun <T> DataStore<Preferences>.getValue(key: Preferences.Key<T>, defaultValue: T): Flow<T> =
    data.map {
        it[key] ?: defaultValue
    }

/**
 * 读取枚举值
 */
inline fun <reified E : Enum<E>> DataStore<Preferences>.getEnum(
    key: Preferences.Key<String>,
    defaultValue: E
): Flow<E> = data.map { pref ->
    val name = pref[key] ?: return@map defaultValue
    runCatching { enumValueOf<E>(name) }.getOrDefault(defaultValue)
}

/**
 * 写入普通值
 */
suspend fun <T> DataStore<Preferences>.setValue(key: Preferences.Key<T>, value: T) {
    edit { it[key] = value }
}

/**
 * 写入枚举值
 */
suspend fun <E : Enum<E>> DataStore<Preferences>.setEnum(key: Preferences.Key<String>, value: E) {
    edit { it[key] = value.name }
}