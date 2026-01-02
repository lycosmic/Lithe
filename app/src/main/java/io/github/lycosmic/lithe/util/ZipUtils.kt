package io.github.lycosmic.lithe.util

import android.content.Context
import android.net.Uri
import io.github.lycosmic.lithe.log.logD
import io.github.lycosmic.lithe.log.logE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.security.MessageDigest
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

object ZipUtils {

    /**
     * 在 Zip 文件中查找指定路径的 Zip 条目并执行操作
     * @param targetPath Zip 内部的相对路径
     */
    suspend fun <T> findZipEntryAndAction(
        context: Context,
        uri: Uri,
        targetPath: String,
        action: suspend
            (InputStream) -> T
    ): T? = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                // 增加缓冲流
                ZipInputStream(inputStream.buffered()).use { zipInputStream ->
                    var entry: ZipEntry? = zipInputStream.nextEntry
                    while (entry != null) {
                        // 忽略目录
                        if (!entry.isDirectory && entry.name.equals(
                                targetPath,
                                ignoreCase = false
                            )
                        ) {
                            return@withContext action(zipInputStream)
                        }
                        zipInputStream.closeEntry()
                        entry = zipInputStream.nextEntry
                    }
                }
            }
        } catch (e: Exception) {
            when (e) {
                is FileNotFoundException ->
                    logE(e = e) {
                        "Zip file not found: $uri"
                    }

                is IOException -> logE(e = e) {
                    "IO error reading zip: $targetPath"
                }

                else -> logE(e = e) {
                    "Unexpected error in ZipUtils"
                }
            }
        }

        return@withContext null
    }

    /**
     * 将 Zip 中的封面提取到应用存储目录
     */
    suspend fun extractCoverToStorage(
        context: Context,
        uri: Uri,
        coverZipPath: String,
    ): String? = withContext(Dispatchers.IO) {
        val cacheDir = File(context.filesDir, "covers")
        if (!cacheDir.exists()) cacheDir.mkdirs()

        val cacheKey = generateCacheKey(uri.toString(), coverZipPath)
        val destFile = File(cacheDir, "$cacheKey.jpg")

        if (destFile.exists() && destFile.length() > 0) {
            logD {
                "Cache hit: ${destFile.absolutePath}"
            }
            return@withContext destFile.absolutePath
        }

        return@withContext copyZipEntryToFile(context, uri, destFile, coverZipPath)
    }


    /**
     * 将 Zip 中的封面提取到缓存目录
     */
    suspend fun extractCoverToCache(
        context: Context,
        uri: Uri,
        coverZipPath: String,
    ): String? = withContext(Dispatchers.IO) {
        val cacheDir = File(context.cacheDir, "reader_images")
        if (!cacheDir.exists()) cacheDir.mkdirs()

        // 生成稳定的缓存文件名
        val cacheKey = generateCacheKey(uri.toString(), coverZipPath)
        val destFile = File(cacheDir, "$cacheKey.jpg")

        // 检查缓存是否存在且有效
        if (destFile.exists() && destFile.length() > 0) {
            logD {
                "Cache hit: ${destFile.absolutePath}"
            }
            return@withContext destFile.absolutePath
        }

        // 提取文件
        return@withContext copyZipEntryToFile(context, uri, destFile, coverZipPath)
    }

    /**
     * 将 Zip 中的文件直接拷贝到目标文件/目录
     */
    private suspend fun copyZipEntryToFile(
        context: Context,
        uri: Uri,
        destFile: File,
        zipPath: String,
    ): String? {
        return findZipEntryAndAction(context, uri, zipPath) { inputStream ->

            var tmpFile: File? = null

            try {
                // 临时文件机制
                tmpFile = File(destFile.parent, "${destFile.name}.tmp")

                FileOutputStream(tmpFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }

                // 写入成功后重命名为正式文件
                if (destFile.exists()) destFile.delete()
                tmpFile.renameTo(destFile)

                return@findZipEntryAndAction destFile.absolutePath
            } catch (e: IOException) {
                // 删除临时文件
                tmpFile?.delete()
                logE(e = e) {
                    "Error writing cover to storage"
                }
                return@findZipEntryAndAction null
            }
        }
    }


    /**
     * 使用 MD5/Hash 生成简单的唯一文件名
     */
    private fun generateCacheKey(uriPart: String, pathPart: String): String {
        val key = "$uriPart-$pathPart"
        return try {
            val bytes = MessageDigest.getInstance("MD5").digest(key.toByteArray())
            return buildString {
                for (b in bytes) {
                    append(String.format("%02x", b))
                }
            }
        } catch (_: Exception) {
            (uriPart.hashCode() + pathPart.hashCode()).toString()
        }
    }

}