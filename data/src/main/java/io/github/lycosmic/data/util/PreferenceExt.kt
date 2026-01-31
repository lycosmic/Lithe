package io.github.lycosmic.data.util

import androidx.datastore.preferences.core.Preferences

internal inline fun <reified E : Enum<E>> Preferences.getEnum(
    key: Preferences.Key<String>,
    defaultValue: E
): E {
    return runCatching {
        val name = this[key] ?: return defaultValue
        enumValueOf<E>(name)
    }.getOrDefault(defaultValue)
}