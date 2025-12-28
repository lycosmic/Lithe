package io.github.lycosmic.lithe.data.repository

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import io.github.lycosmic.lithe.data.local.dao.DirectoryDao
import io.github.lycosmic.lithe.data.local.entity.AuthorizedDirectory
import io.github.lycosmic.lithe.data.model.FileFormat
import io.github.lycosmic.lithe.data.model.FileItem
import io.github.lycosmic.lithe.utils.UriUtils
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.supervisorScope
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
    fun getDirectories(): Flow<List<AuthorizedDirectory>> {
        return directoryDao.getScannedDirectoriesFlow()
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

        val authorizedDirectory = AuthorizedDirectory(
            uri = uri.toString(),
            root = rootName,
            path = relativePath
        )

        directoryDao.insertDirectory(authorizedDirectory)
    }

    /**
     * 移除文件夹
     */
    suspend fun removeDirectory(directory: AuthorizedDirectory) = withContext(Dispatchers.IO) {
        directoryDao.deleteDirectory(directory)
    }


    /**
     * 扫描所有已授权文件夹下的书籍,用于自动刷新场景,文件夹列表由 ViewModel 提供
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun scanAllBooks(directories: List<AuthorizedDirectory>): List<FileItem> =
        withContext(Dispatchers.IO) {
            supervisorScope { // 为了不影响其他任务
                val allFiles = mutableListOf<FileItem>()

                val subDeferredResults = mutableListOf<Deferred<List<FileItem>>>()

                directories.forEach { dir ->
                    val deferred = async {
                        val rootUri = dir.uri.toUri()
                        val rootDocId = DocumentsContract.getTreeDocumentId(rootUri)

                        // 递归扫描该文件夹
                        fastRecursiveScan(
                            treeUri = rootUri,
                            parentDocId = rootDocId,
                            currentDisplayPath = dir.path
                        )
                    }
                    subDeferredResults.add(deferred)
                }

                val subFileItems = subDeferredResults.awaitAll().flatten()

                allFiles.addAll(subFileItems)
                allFiles
            }

        }


    /**
     * 使用 DocumentFile 进行递归扫描文件夹
     */
    private suspend fun recursiveScan(
        doc: DocumentFile,
        currentDisplayPath: String, // 当前路径
    ): List<FileItem> = supervisorScope {
        // 结果列表
        val resultList = mutableListOf<FileItem>()
        // 子任务列表
        val subDirJobs = mutableListOf<Deferred<List<FileItem>>>()

        val files = doc.listFiles()

        for (file in files) {
            if (file.isDirectory) {
                // 递归进入子文件夹
                val nextPath = "$currentDisplayPath/${file.name}"
                val deferred = async {
                    recursiveScan(file, nextPath)
                }

                subDirJobs.add(deferred)
            } else {
                // 这是一个文件
                val name = file.name?.substringAfterLast(".") ?: continue
                val type = getFileType(name)

                if (type == FileFormat.UNKNOWN) {
                    continue
                }

                resultList.add(
                    FileItem(
                        name = name,
                        uri = file.uri,
                        type = type,
                        size = file.length(),
                        lastModified = file.lastModified(),
                        parentPath = currentDisplayPath // 记录它是属于哪个文件夹的
                    )
                )
            }
        }

        // 合并子任务的结果
        val subDirResults = subDirJobs.awaitAll().flatten()
        resultList.addAll(subDirResults)

        return@supervisorScope resultList
    }

    /**
     * 使用 ContentResolver 进行极速递归扫描
     */
    private suspend fun fastRecursiveScan(
        treeUri: Uri, // 授权目录的 URI
        parentDocId: String, // 授权目录的标识符
        currentDisplayPath: String
    ): List<FileItem> = supervisorScope {

        val resultList = mutableListOf<FileItem>()
        val subDirJobs = mutableListOf<Deferred<List<FileItem>>>()

        // 子文件列表的 URI
        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, parentDocId)

        // 查询的列
        val projection = arrayOf(
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_MIME_TYPE,
            DocumentsContract.Document.COLUMN_SIZE,
            DocumentsContract.Document.COLUMN_LAST_MODIFIED
        )

        try {
            application.contentResolver.query(
                childrenUri,
                projection,
                null, null, null
            )?.use { cursor ->
                val idCol = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
                val nameCol = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                val mimeCol = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_MIME_TYPE)
                val sizeCol = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_SIZE)
                val dateCol = cursor.getColumnIndex(DocumentsContract.Document.COLUMN_LAST_MODIFIED)

                while (cursor.moveToNext()) {
                    val docId = cursor.getString(idCol)
                    val name = cursor.getString(nameCol) ?: continue
                    val mimeType = cursor.getString(mimeCol)
                    val size = cursor.getLong(sizeCol)
                    val lastMod = cursor.getLong(dateCol)

                    // 如果是文件夹
                    if (mimeType == DocumentsContract.Document.MIME_TYPE_DIR) {
                        val nextPath = "$currentDisplayPath/$name"
                        val job = async {
                            fastRecursiveScan(treeUri, docId, nextPath)
                        }
                        subDirJobs.add(job)
                    } else {
                        // 判断文件类型
                        val type = getFileType(name)

                        if (type != FileFormat.UNKNOWN) {
                            // 构建文件 URI
                            val fileUri =
                                DocumentsContract.buildDocumentUriUsingTree(treeUri, docId)

                            resultList.add(
                                FileItem(
                                    name = name,
                                    uri = fileUri,
                                    type = type,
                                    size = size,
                                    lastModified = lastMod,
                                    parentPath = currentDisplayPath
                                )
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 等待所有子文件夹扫描完成，并合并结果
        val subDirResults = subDirJobs.awaitAll().flatten()
        resultList.addAll(subDirResults)

        return@supervisorScope resultList
    }

    /**
     * 获取文件类型
     */
    private fun getFileType(name: String): FileFormat {
        return when {
            name.endsWith(FileFormat.EPUB_EXTENSION, true) -> FileFormat.EPUB
            name.endsWith(FileFormat.PDF_EXTENSION, true) -> FileFormat.PDF
            name.endsWith(FileFormat.TXT_EXTENSION, true) -> FileFormat.TXT
            else -> FileFormat.UNKNOWN
        }
    }
}


