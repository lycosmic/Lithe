package io.github.lycosmic.lithe.util

import android.net.Uri
import android.provider.DocumentsContract


object UriUtils {
    /**
     * 解析 Uri 为路径信息
     * @return 根路径和相对路径
     */
    fun parseUriToPath(uri: Uri): Pair<String, String> {
        try {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getTreeDocumentId(uri)
                val split = docId.split(":")

                val type = split[0]
                val relativePath = if (split.size > 1) split[1] else ""

                val rootPath = if ("primary".equals(other = type, ignoreCase = true)) {
                    "/storage/emulated/0"
                } else {
                    // SD 卡挂载点通常是 /storage/UUID
                    "/storage/$type"
                }

                return Pair(rootPath, relativePath)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Pair("Unknown", "Unknown")
    }

    /**
     * 判断是否为外部存储
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

}