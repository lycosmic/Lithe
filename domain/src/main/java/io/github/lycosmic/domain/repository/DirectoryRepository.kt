package io.github.lycosmic.domain.repository

import io.github.lycosmic.domain.model.Directory
import io.github.lycosmic.domain.model.FileItem
import kotlinx.coroutines.flow.Flow


interface DirectoryRepository {

    /**
     * 获取已授权文件夹列表
     */
    fun getDirectoriesFlow(): Flow<List<Directory>>

    /**
     * 添加文件夹
     */
    suspend fun insertDirectory(uriString: String): Long

    /**
     * 移除文件夹
     */
    suspend fun removeDirectory(directory: Directory)

    /**
     * 扫描获取所有已授权文件夹下的文件列表
     */
    suspend fun scanFilesInDirectories(directories: List<Directory>): List<FileItem>
}