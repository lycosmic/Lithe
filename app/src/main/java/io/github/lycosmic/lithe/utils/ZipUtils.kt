package io.github.lycosmic.lithe.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object ZipUtils {

    /**
     * 处理 Zip 条目
     * @param targetPath 目标文件相对路径
     */
    suspend fun <T> findZipEntryAndAction(
        context: Context,
        uri: Uri,
        targetPath: String,
        action: suspend
            (InputStream) -> T
    ): T? = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(uri).use { inputStream ->
                ZipInputStream(inputStream?.buffered()).use { zipInputStream ->
                    var entry: ZipEntry? = zipInputStream.nextEntry
                    while (entry != null) {
                        if (entry.name.equals(targetPath, ignoreCase = false)) {
                            return@withContext action(zipInputStream)
                        }
                        zipInputStream.closeEntry()
                        entry = zipInputStream.nextEntry
                    }
                }
            }
        } catch (e: Exception) {
            when (e) {
                is FileNotFoundException -> Log.e("ZipUtils", "File not found: $uri", e)
                is IOException -> Log.e("ZipUtils", "IO Error reading zip: ${e.message}", e)
                else -> Log.e("ZipUtils", "Unexpected error: ${e.message}", e)
            }
        }

        return@withContext null
    }
}