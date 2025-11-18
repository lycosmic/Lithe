package io.github.lycosmic.lithe.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.History
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.lycosmic.lithe.presentation.navigation.AppRoutes

enum class AppTabs(
    val route: AppRoutes,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    // 书库
    Library(
        route = AppRoutes.Library,
        label = "书库",
        selectedIcon = Icons.Filled.Book,
        unselectedIcon = Icons.Outlined.Book
    ),

    // 历史
    History(
        route = AppRoutes.History,
        label = "历史",
        selectedIcon = Icons.Filled.History,
        unselectedIcon = Icons.Outlined.History
    ),

    // 浏览
    Browse(
        route = AppRoutes.Browse,
        label = "浏览",
        selectedIcon = Icons.Filled.Explore,
        unselectedIcon = Icons.Outlined.Explore
    );

    companion object {
        /**
         * 获取路由所属的底部标签
         */
        fun getTabByRoute(route: Any?): AppTabs? {
            return entries.find {
                it.route == route
            }
        }
    }
}