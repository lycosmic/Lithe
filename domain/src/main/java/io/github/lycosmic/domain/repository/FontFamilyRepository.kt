package io.github.lycosmic.domain.repository

import io.github.lycosmic.domain.model.AppFontFamily
import io.github.lycosmic.domain.model.AppFontWeight
import kotlinx.coroutines.flow.Flow

/**
 * 字体系列仓库
 */
interface FontFamilyRepository {
    /**
     * 获取可用的字体系列
     */
    fun getAvailableFonts(): Flow<List<AppFontFamily>>

    /**
     * 获取指定 ID 的字体系列
     */
    fun getFontFamily(
        fontId: String,
        fontWeight: AppFontWeight,
        isItalic: Boolean
    ): Any // 返回 Any 以保持平台无关，实现层再强转
}