package io.github.lycosmic.lithe.domain.repository

import android.net.Uri
import io.github.lycosmic.lithe.data.local.entity.AuthorizedDirectory
import io.github.lycosmic.lithe.data.model.FileItem
import kotlinx.coroutines.flow.Flow

interface DirectoryRepository {
    /**
     * 获取已授权文件夹列表
     */
    fun getDirectoriesFlow(): Flow<List<AuthorizedDirectory>>


    /**
     * 添加文件夹
     */
    suspend fun insertDirectory(uri: Uri): Long

    /**
     * 移除文件夹
     */
    suspend fun removeDirectory(directory: AuthorizedDirectory)

    /**
     * 扫描获取所有已授权文件夹下的文件列表
     */
    suspend fun scanFilesInDirectories(directories: List<AuthorizedDirectory>): List<FileItem>
}