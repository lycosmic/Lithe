package io.github.lycosmic.data.util

import org.xmlpull.v1.XmlPullParser


/**
 * 安全读取 Text，防止 XML 格式不标准导致崩溃
 */
fun XmlPullParser.safeNextText(): String {
    var result = ""
    val nextType = next()
    if (nextType == XmlPullParser.TEXT) {
        result = text
        nextTag() // 移至 END_TAG
    }
    return result
}