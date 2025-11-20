package io.github.lycosmic.lithe.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.log10
import kotlin.math.pow

object FileUtils {

    /**
     * 格式化文件大小
     */
    fun formatFileSize(size: Long): String {
        if (size <= 0) return "0 B"

        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        // 获取文件大小单位
        val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
        val unit = units[digitGroups]

        return String.format(
            Locale.getDefault(),
            "%.2f %s",
            size / 1024.0.pow(digitGroups),
            unit
        )
    }


    /**
     * 格式化日期
     */
    @SuppressLint("SimpleDateFormat")
    fun formatDate(timeStamp: Long): String {
        return SimpleDateFormat("HH:mm dd MM月 yyyy", Locale.getDefault())
            .format(Date(timeStamp))
    }

}