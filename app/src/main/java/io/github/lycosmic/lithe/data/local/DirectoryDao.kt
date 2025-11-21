package io.github.lycosmic.lithe.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.lycosmic.lithe.data.model.ScannedDirectory
import kotlinx.coroutines.flow.Flow


@Dao
interface DirectoryDao {
    /**
     * 获取所有已扫描的目录
     */
    @Query("SELECT * FROM scanned_directories ORDER BY addedTime DESC")
    fun getScannedDirectoriesFlow(): Flow<List<ScannedDirectory>>

    /**
     * 一次性获取所有已扫描的目录
     */
    @Query("SELECT * FROM scanned_directories ORDER BY addedTime DESC")
    fun getScannedDirectoriesSnapshot(): List<ScannedDirectory>

    /**
     * 添加目录
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDirectory(directory: ScannedDirectory)

    /**
     * 删除目录
     */
    @Delete
    suspend fun deleteDirectory(directory: ScannedDirectory)
}