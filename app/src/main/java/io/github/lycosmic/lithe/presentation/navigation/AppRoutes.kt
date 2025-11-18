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

    // 设置
    @Serializable
    object Settings : AppRoutes

    // 阅读
    @Serializable
    data class Reader(val bookId: Long) : AppRoutes
}