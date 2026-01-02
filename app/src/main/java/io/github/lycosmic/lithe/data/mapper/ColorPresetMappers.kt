package io.github.lycosmic.lithe.data.mapper

import io.github.lycosmic.lithe.data.local.entity.ColorPresetEntity
import io.github.lycosmic.model.ColorPreset

/**
 * 将 Domain 对象转换为数据库实体
 */
fun ColorPreset.toEntity(order: Int): ColorPresetEntity {
    return ColorPresetEntity(
        id = if (this.id == -1L) null else this.id,
        name = this.name,
        backgroundColorArgb = this.backgroundColorArgb,
        textColorArgb = this.textColorArgb,
        sortOrder = order
    )
}

/**
 * 将数据库实体转换为 Domain 对象
 */
fun ColorPresetEntity.toDomain(): ColorPreset {
    return ColorPreset(
        id = this.id ?: -1L,
        name = this.name,
        backgroundColorArgb = this.backgroundColorArgb,
        textColorArgb = this.textColorArgb
    )
}