package io.github.lycosmic.lithe.data.repository

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import io.github.lycosmic.lithe.data.local.DirectoryDao
import io.github.lycosmic.lithe.data.model.FileType
import io.github.lycosmic.lithe.data.model.ScannedDirectory
import io.github.lycosmic.lithe.data.model.SelectableFileItem
import io.github.lycosmic.lithe.utils.UriUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
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
    suspend fun addDirectory(uri: Uri) = withContext(Dispatchers.IO) {
        val resolver = application.contentResolver

        try {
            // 获取文件夹的持久化读写权限
            val takeFlags: Int =
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            resolver.takePersistableUriPermission(uri, takeFlags)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext
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
    suspend fun removeDirectory(directory: ScannedDirectory) = withContext(Dispatchers.IO) {
        directoryDao.deleteDirectory(directory)
    }

    /**
     * 扫描所有已授权文件夹下的书籍
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun scanAllBooks(): List<SelectableFileItem> = withContext(Dispatchers.IO) {
        val allFiles = mutableListOf<SelectableFileItem>()

        val directories = directoryDao.getScannedDirectories().first()

        for (dir in directories) {
            val rootUri = dir.uri.toUri()
            val rootDocument =
                DocumentFile.fromTreeUri(application.applicationContext, rootUri) ?: continue

            // 递归扫描该文件夹
            recursiveScan(
                rootDocument,
                dir.root,
                allFiles
            )
        }

        return@withContext allFiles
    }


    /**
     * 递归扫描文件夹
     */
    private fun recursiveScan(
        doc: DocumentFile,
        currentDisplayPath: String, // 当前路径
        resultList: MutableList<SelectableFileItem>
    ) {
        val files = doc.listFiles()

        for (file in files) {
            if (file.isDirectory) {
                // 递归进入子文件夹
                val nextPath = "$currentDisplayPath/${file.name}"
                recursiveScan(file, nextPath, resultList)
            } else {
                // 这是一个文件
                val name = file.name ?: continue
                val type = when {
                    name.endsWith(".${FileType.EPUB.value}", true) -> FileType.EPUB
                    name.endsWith(".${FileType.PDF.value}", true) -> FileType.PDF
                    name.endsWith(".${FileType.TXT.value}", true) -> FileType.TXT
                    else -> FileType.UNKNOWN
                }

                if (type == FileType.UNKNOWN) {
                    continue
                }


                resultList.add(
                    SelectableFileItem(
                        name = name,
                        uri = file.uri,
                        type = type,
                        size = file.length(),
                        selected = false,
                        lastModified = file.lastModified(),
                        parentPath = currentDisplayPath // 记录它是属于哪个文件夹的
                    )
                )
            }
        }
    }

}