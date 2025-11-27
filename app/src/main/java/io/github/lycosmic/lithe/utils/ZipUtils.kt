package io.github.lycosmic.lithe.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.UUID
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
                is FileNotFoundException -> Timber.e(
                    e,
                    "Zip file not found, uri is %s, zip path is %s",
                    uri.toString(), targetPath
                )

                is IOException -> Timber.e(e, "reading zip IO error: %s", e.message)
                else -> Timber.e(e, "Unexpected error: %s", e.message)
            }
        }

        return@withContext null
    }


    /**
     * 将指定 Zip 相对路径的图片保存应用私有目录
     */
    suspend fun extractCoverToStorage(
        context: Context,
        uri: Uri,
        coverZipPath: String,
    ): String? = withContext(Dispatchers.IO) {
        val coversDir = File(context.filesDir, "covers")
        return@withContext extractCoverToDir(context, coversDir, uri, coverZipPath)
    }

    /**
     * 将指定 Zip 相对路径的图片保存到缓存目录
     */
    suspend fun extractCoverToCache(
        context: Context,
        uri: Uri,
        coverZipPath: String,
    ): String? = withContext(Dispatchers.IO) {
        val imagesDir = File(context.cacheDir, "reader_images")

        // 如果缓存里已经有了，直接返回路径
        if (imagesDir.exists() && imagesDir.length() > 0) {
            return@withContext imagesDir.absolutePath
        }
        return@withContext extractCoverToDir(context, imagesDir, uri, coverZipPath)
    }

    /**
     * 将指定 Zip 相对路径的图片保存到指定目录
     */
    suspend fun extractCoverToDir(
        context: Context,
        dir: File, // 目标目录
        uri: Uri,
        coverZipPath: String,
    ): String? = withContext(Dispatchers.IO) {
        // 目录
        dir
        if (!dir.exists()) dir.mkdirs()

        // 生成安全的文件名
        val fileName = UUID.randomUUID().toString().replace("-", "")
        val destFile = File(dir, "cover_${fileName}_${System.currentTimeMillis()}.jpg")

        return@withContext findZipEntryAndAction(
            context,
            uri,
            coverZipPath
        ) { inputStream ->
            // 保存到本地
            FileOutputStream(destFile).use { fos ->
                // 压缩图片
                val bitmap = BitmapFactory.decodeStream(inputStream)
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 80, fos)
                bitmap?.recycle()
            }
            return@findZipEntryAndAction destFile.absolutePath
        } ?: run {
            // 删除文件
            destFile.delete()
            null
        }
    }
}