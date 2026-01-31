package io.github.lycosmic.domain.repository

import java.lang.reflect.Type

interface JsonParser {
    fun <T> fromJson(json: String, classOfT: Class<T>): T?
    fun <T> fromJson(json: String, typeOfT: Type): T?
    fun <T> toJson(src: T): String
}