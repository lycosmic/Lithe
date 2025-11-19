package io.github.lycosmic.lithe.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface DirectoryDao {
    /**
     * 获取所有已扫描的目录
     */
    @Query("SELECT * FROM scanned_directories ORDER BY addedTime DESC")
    fun getScannedDirectories(): Flow<List<ScannedDirectory>>

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