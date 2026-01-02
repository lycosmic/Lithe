package io.github.lycosmic.lithe.util

import android.content.Context
import android.net.Uri


/**
 * 获取文件大小
 */
fun Uri.getFileSize(context: Context): Result<Long> {
    return runCatching {
        context.contentResolver.openFileDescriptor(this, "r")?.use {
            it.statSize
        } ?: throw IllegalArgumentException("无法打开文件")
    }
}