package io.github.lycosmic.lithe.domain.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import io.github.lycosmic.lithe.data.local.entity.ColorPresetEntity


@Immutable
data class ColorPreset(
    val id: Long,
    val name: String,
    val backgroundColor: Color,
    val textColor: Color,
    val isSelected: Boolean = false
) {
    companion object {
        val defaultColorPreset = ColorPreset(
            id = -1,
            name = "",
            backgroundColor = Color(0xFFFAF8FF), // TODO: 硬编码
            textColor = Color(0xFF44464F)
        )

        /**
         * 转换为数据库实体
         */
        fun ColorPreset.toEntity(order: Int): ColorPresetEntity {
            return ColorPresetEntity(
                id = if (this.id == -1L) null else this.id,
                name = this.name,
                backgroundColorArgb = this.backgroundColor.toArgb(),
                textColorArgb = this.textColor.toArgb(),
                sortOrder = order
            )
        }
    }
}