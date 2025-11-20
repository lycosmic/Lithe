package io.github.lycosmic.lithe.data.repository

import android.app.Application
import android.content.Intent
import android.net.Uri
import io.github.lycosmic.lithe.data.local.DirectoryDao
import io.github.lycosmic.lithe.data.local.ScannedDirectory
import io.github.lycosmic.lithe.utils.UriUtils
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class DirectoryRepository @Inject constructor(
    private val directoryDao: DirectoryDao,
    private val application: Application
) {
    /**
     * 获取已添加的文件夹列表
     */
    fun getDirectories(): Flow<List<ScannedDirectory>> {
        return directoryDao.getScannedDirectories()
    }

    /**
     * 添加文件夹
     */
    suspend fun addDirectory(uri: Uri) {
        val resolver = application.contentResolver

        try {
            // 获取文件夹的持久化读写权限
            resolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }

        // 获取文件的路径信息
        val (rootName, relativePath) = UriUtils.parseUriToPath(uri)

        val scannedDirectory = ScannedDirectory(
            uri = uri.toString(),
            root = rootName,
            path = relativePath
        )

        directoryDao.insertDirectory(scannedDirectory)
    }

    /**
     * 移除文件夹
     */
    suspend fun removeDirectory(directory: ScannedDirectory) {
        directoryDao.deleteDirectory(directory)
    }
}