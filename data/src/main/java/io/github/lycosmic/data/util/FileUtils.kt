package io.github.lycosmic.data.util

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

object FileUtils {

    /**
     * 将 Uri 转换为可操作的 File 对象
     * 如果是 content://，则复制到缓存目录；如果是 file://，则直接返回
     */
    fun getFileFromUri(context: Context, uriString: String): File? {
        val uri = Uri.parse(uriString)

        if (uri.scheme == "file") {
            return File(uri.path ?: return null)
        }

        return copyContentUriToCache(context, uri)
    }

    private fun copyContentUriToCache(context: Context, uri: Uri): File? {
        try {
            // 获取文件名
            val fileName = "temp_book_${System.currentTimeMillis()}"

            // 在应用的私有缓存目录下创建文件
            val cacheFile = File(context.cacheDir, fileName)

            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(cacheFile).use { output ->
                    input.copyTo(output)
                }
            }

            return cacheFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}