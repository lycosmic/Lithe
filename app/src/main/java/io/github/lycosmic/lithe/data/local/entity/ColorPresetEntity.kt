package io.github.lycosmic.lithe.data.local.entity

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * 阅读页面的颜色预设
 */
@Entity(tableName = "color_presets")
@Immutable
@Serializable
data class ColorPresetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val name: String,
    val backgroundColorArgb: Int, // 颜色值 ARGB
    val textColorArgb: Int,
    val sortOrder: Int = 0 // 排序
)
