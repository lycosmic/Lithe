package io.github.lycosmic.lithe.util

import java.math.BigDecimal
import java.math.RoundingMode
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

    /**
     * 格式化进度
     * @param progress 进度 0-1
     * @param digits 小数位数
     * @param withPercent 是否加上百分号
     * @return 进度字符串，例如 "42.9%"、"0%"
     */
    fun formatProgress(progress: Float, digits: Int = 1, withPercent: Boolean = true): String {
        // 处理边界
        val safeProgress = progress.coerceIn(0f, 1f)

        // 转为 BigDecimal
        val value = BigDecimal(safeProgress.toString()) // 转String避免精度丢失
            .movePointRight(2)

        // 设置精度和四舍五入
        val roundedValue = value.setScale(digits, RoundingMode.HALF_UP)
        val text = roundedValue.toPlainString() // 防止科学计数法

        return if (withPercent) "$text%" else text
    }
}