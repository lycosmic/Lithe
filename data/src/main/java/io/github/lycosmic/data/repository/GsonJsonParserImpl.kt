package io.github.lycosmic.data.repository

import com.google.gson.Gson
import io.github.lycosmic.domain.repository.JsonParser
import io.github.lycosmic.domain.util.DomainLogger
import java.lang.reflect.Type
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GsonJsonParserImpl @Inject constructor(
    private val gson: Gson,
    private val logger: DomainLogger
) : JsonParser {
    override fun <T> fromJson(json: String, classOfT: Class<T>): T? {
        return try {
            gson.fromJson(json, classOfT)
        } catch (e: Exception) {
            logger.e(throwable = e) {
                "解析 JSON 字符串失败: $json"
            }
            null
        }
    }

    override fun <T> fromJson(json: String, typeOfT: Type): T? {
        return try {
            gson.fromJson(json, typeOfT)
        } catch (e: Exception) {
            logger.e(throwable = e) {
                "解析 JSON 字符串失败: $json"
            }
            null
        }
    }

    override fun <T> toJson(src: T): String {
        return gson.toJson(src)
    }
}