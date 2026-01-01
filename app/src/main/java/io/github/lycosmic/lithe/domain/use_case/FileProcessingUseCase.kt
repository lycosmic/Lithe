package io.github.lycosmic.lithe.domain.use_case

import io.github.lycosmic.lithe.data.model.FileItem
import io.github.lycosmic.lithe.data.repository.DirectoryRepositoryImpl
import io.github.lycosmic.lithe.domain.model.FilterOption
import io.github.lycosmic.lithe.domain.model.SortType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 文件处理用例
 */

@Singleton
class FileProcessingUseCase @Inject constructor(
    private val directoryRepositoryImpl: DirectoryRepositoryImpl,
) {

    /**
     * 扫描所有已授权文件夹下的文件
     */
    fun scanFiles(): Flow<List<FileItem>> {
        return directoryRepositoryImpl.getDirectoriesFlow()
            .map { directories ->
                directoryRepositoryImpl.scanFilesInDirectories(directories)
            }
    }

    /**
     * 处理文件，包括筛选、排序和分组
     */
    fun processFiles(
        rawFiles: List<FileItem>,
        sortType: SortType,
        isAscending: Boolean,
        filters: List<FilterOption>,
        searchText: String,
        dbFileUris: Set<String>
    ): Map<String, List<FileItem>> {
        // 文件类型筛选
        val filteredFiles = if (isFilterValid(filters)) {
            rawFiles.filter { file ->
                filters.any { filter ->
                    file.type == filter.format && filter.isSelected
                }
            }
        } else {
            rawFiles
        }

        // 过滤掉已导入的文件
        val filteredImportedFiles = filteredFiles.filter { file ->
            !dbFileUris.contains(file.uri.toString())
        }

        // 关键词筛选
        val searchResult = if (searchText.isNotEmpty()) {
            filteredImportedFiles.filter { item ->
                item.name.contains(searchText)
            }
        } else {
            filteredImportedFiles
        }

        // 排序
        val sortedFiles = sortFiles(searchResult, sortType, isAscending)

        // 按父文件夹分组
        return sortedFiles.groupBy { item ->
            item.parentPath
        }
    }

    /**
     * 排序文件列表
     */
    private fun sortFiles(
        files: List<FileItem>,
        type: SortType,
        ascending: Boolean
    ): List<FileItem> {
        if (files.isEmpty()) return emptyList()

        val comparator = when (type) {
            SortType.ALPHABETICAL -> compareBy<FileItem> { it.name }
            SortType.SIZE -> compareBy { it.size }
            SortType.LAST_MODIFIED -> compareBy { it.lastModified }
            SortType.FILE_FORMAT -> compareBy { it.type.value }
        }

        return if (ascending) {
            files.sortedWith(comparator)
        } else {
            files.sortedWith(comparator).reversed()
        }
    }

    /**
     * 验证文件类型筛选条件是否有效
     */
    private fun isFilterValid(filters: List<FilterOption>): Boolean {
        return filters.any { it.isSelected }
    }

}