package io.github.lycosmic.lithe.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


/**
 * 应用所有的路由
 */
@Serializable
sealed interface AppRoutes : NavKey {

    // 书库
    @Serializable
    object Library : AppRoutes

    // 历史
    @Serializable
    object History : AppRoutes

    // 浏览
    @Serializable
    object Browse : AppRoutes

    // 阅读
    @Serializable
    data class Reader(val bookId: Long) : AppRoutes

    // 设置主页
    @Serializable
    object Settings : AppRoutes

    // 浏览设置
    @Serializable
    object SettingsBrowse : AppRoutes

    // 常规设置
    @Serializable
    object SettingsGeneral : AppRoutes

    // 外观设置
    @Serializable
    object SettingsAppearance : AppRoutes

    // 阅读器设置
    @Serializable
    object SettingsReader : AppRoutes

    // 书库设置
    @Serializable
    object SettingsLibrary : AppRoutes

    // 书籍详情
    @Serializable
    data class BookDetail(val bookId: Long) : AppRoutes
    companion object {
        // 默认路由
        val defaultRoute = Library
    }
}