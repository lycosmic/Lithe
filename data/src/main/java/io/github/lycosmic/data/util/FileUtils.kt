package io.github.lycosmic.data.util

import android.content.Context
import androidx.core.net.toUri
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

object FileUtils {

    /**
     * 将 Uri 转换为 InputStream
     */
    fun openInputStream(context: Context, uriString: String): InputStream? {
        val uri = uriString.toUri()

        return when (uri.scheme) {
            "file" -> {
                FileInputStream(File(uri.path ?: return null))
            }

            "content" -> {
                context.contentResolver.openInputStream(uri)
            }

            else -> null
        }
    }
}