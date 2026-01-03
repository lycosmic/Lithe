package io.github.lycosmic.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import io.github.lycosmic.data.local.entity.ColorPresetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ColorPresetDao {
    /**
     * 获取所有颜色预设
     */
    @Query("SELECT * FROM color_presets ORDER BY sortOrder ASC")
    fun getAllPresets(): Flow<List<ColorPresetEntity>>

    /**
     * 同步获取所有的颜色预设
     */
    @Query("SELECT * FROM color_presets ORDER BY sortOrder ASC")
    suspend fun getAllPresetsSync(): List<ColorPresetEntity>

    /**
     * 获取当前最大的排序号
     * 如果没有数据，则返回 null
     */
    @Query("SELECT MAX(sortOrder) FROM color_presets")
    suspend fun getMaxSortOrder(): Int?

    /**
     * 获取指定 ID 的颜色预设排序
     */
    @Query("SELECT sortOrder FROM color_presets WHERE id = :id")
    suspend fun getPresetSortOrderById(id: Long): Int?

    /**
     * 新增颜色预设
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPreset(preset: ColorPresetEntity)

    /**
     * 删除颜色预设
     */
    @Delete
    suspend fun deletePreset(preset: ColorPresetEntity)

    /**
     * 更新颜色预设
     */
    @Update
    suspend fun updatePreset(preset: ColorPresetEntity)

    /**
     * 批量更新颜色预设，用于拖拽颜色预设后，保存新的排序
     */
    @Update
    suspend fun updatePresets(presets: List<ColorPresetEntity>)
}