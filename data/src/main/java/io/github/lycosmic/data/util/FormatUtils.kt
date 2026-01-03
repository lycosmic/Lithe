package io.github.lycosmic.data.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.log10
import kotlin.math.pow

object FormatUtils {

    const val DEFAULT_DATE_PATTERN = "HH:mm dd MM月 yyyy"

    /**
     * 格式化日期
     */
    fun formatDate(timeStamp: Long, pattern: String = DEFAULT_DATE_PATTERN): String {
        return SimpleDateFormat(pattern, Locale.getDefault())
            .format(Date(timeStamp))
    }


    // 文件大小单位
    val units = listOf("B", "KB", "MB", "GB", "TB")

    const val DEFAULT_FORMAT = "%.2f %s"

    /**
     * 格式化文件大小
     */
    fun formatFileSize(size: Long, format: String = DEFAULT_FORMAT): String {
        if (size < 0) {
            throw IllegalArgumentException("文件大小不能小于0")
        }

        // 获取文件大小单位
        val digitGroups =
            (log10(size.toDouble()) / log10(1024.0)).toInt().coerceIn(0, units.size - 1)
        val unit = units[digitGroups]

        return String.format(
            Locale.getDefault(),
            format,
            size / 1024.0.pow(digitGroups),
            unit
        )
    }
}

