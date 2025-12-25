package io.github.lycosmic.lithe.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
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
    fun formatDate(timeStamp: Long, pattern: String = "HH:mm dd MM月 yyyy"): String {
        return SimpleDateFormat(pattern, Locale.getDefault())
            .format(Date(timeStamp))
    }

    /**
     * 获取文件名
     */
    fun getFileName(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    // 查询文件名
                    val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index != -1) {
                        result = it.getString(index)
                    }
                }
            }
        }

        // 获取文件名失败
        if (result == null) {
            // 获取文件名
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result.substring(cut + 1)
            }
        }

        return result ?: "Unknown File.${uri.lastPathSegment}"
    }

}