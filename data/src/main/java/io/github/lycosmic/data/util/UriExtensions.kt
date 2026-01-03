package io.github.lycosmic.data.util

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.OpenableColumns


// 读模式
private const val MODE_READ = "r"

private const val SCHEME_CONTENT = "content"

private const val EXTERNAL_STORAGE_DOCUMENTS = "com.android.externalstorage.documents"

private const val PRIMARY_DOCUMENTS = "primary"

private const val STORAGE_ROOT = "/storage/emulated/0"

/**
 * 获取文件大小
 */
fun Uri.getFileSize(context: Context): Result<Long> {
    return runCatching {
        context.contentResolver.openFileDescriptor(this, MODE_READ)?.use {
            it.statSize
        } ?: throw IllegalArgumentException("打开文件时出错，无法获取文件大小")
    }
}

/**
 * 获取文件名，包括后缀
 */
fun Uri.getFileName(context: Context): Result<String> {
    return runCatching {
        if (this.scheme == SCHEME_CONTENT) {
            val cursor = context.contentResolver.query(this, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    // 查询文件名
                    val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index != -1) {
                        return@runCatching it.getString(index)
                    }
                }
            }
        }

        // 获取文件名失败，尝试从 Uri 路径中获取
        this.path?.let {
            val cut = it.lastIndexOf('/')
            if (cut != -1) {
                return@runCatching it.substring(cut + 1)
            }
        }

        throw IllegalArgumentException("无法获取文件名")
    }
}

/**
 * 获取路径信息
 * @return 根路径和相对路径
 */
fun Uri.parsePathInfo(): Result<Pair<String, String>> {
    /**
     * 判断是否为外部存储
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return EXTERNAL_STORAGE_DOCUMENTS == uri.authority
    }

    return runCatching {
        if (isExternalStorageDocument(this)) {
            val treeDocId = DocumentsContract.getTreeDocumentId(this)
            val split = treeDocId.split(":")

            val storageId = split[0]
            val relativePath = if (split.size > 1) split[1] else ""

            val rootPath = if (PRIMARY_DOCUMENTS.equals(other = storageId, ignoreCase = true)) {
                STORAGE_ROOT
            } else {
                // SD 卡挂载点通常是 /storage/UUID
                "/storage/$storageId"
            }

            return@runCatching Pair(rootPath, relativePath)
        }

        throw IllegalArgumentException("无法解析 Uri")
    }
}

