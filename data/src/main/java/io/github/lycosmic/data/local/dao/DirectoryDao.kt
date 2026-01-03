package io.github.lycosmic.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.lycosmic.data.local.entity.AuthorizedDirectory
import kotlinx.coroutines.flow.Flow

@Dao
interface DirectoryDao {
    /**
     * 获取所有已扫描的目录
     */
    @Query("SELECT ${AuthorizedDirectory.COL_ID}, ${AuthorizedDirectory.COL_URI}, ${AuthorizedDirectory.COL_ROOT}, ${AuthorizedDirectory.COL_PATH}, ${AuthorizedDirectory.COL_ADD_TIME} FROM ${AuthorizedDirectory.TABLE_NAME} ORDER BY ${AuthorizedDirectory.COL_ADD_TIME} DESC")
    fun getScannedDirectoriesFlow(): Flow<List<AuthorizedDirectory>>

    /**
     * 一次性获取所有已扫描的目录
     */
    @Query("SELECT ${AuthorizedDirectory.COL_ID}, ${AuthorizedDirectory.COL_URI}, ${AuthorizedDirectory.COL_ROOT}, ${AuthorizedDirectory.COL_PATH}, ${AuthorizedDirectory.COL_ADD_TIME} FROM ${AuthorizedDirectory.TABLE_NAME} ORDER BY ${AuthorizedDirectory.COL_ADD_TIME} DESC")
    fun getScannedDirectoriesSnapshot(): List<AuthorizedDirectory>

    /**
     * 添加目录
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDirectory(directory: AuthorizedDirectory): Long

    /**
     * 删除目录
     */
    @Delete
    suspend fun deleteDirectory(directory: AuthorizedDirectory)
}