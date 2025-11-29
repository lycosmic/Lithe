package io.github.lycosmic.lithe.ui.navigation

import io.github.lycosmic.lithe.R
import io.github.lycosmic.lithe.presentation.navigation.AppRoutes

enum class AppTabs(
    val route: AppRoutes,
    val label: String,
    val selectedIcon: Int,
    val unselectedIcon: Int
) {
    // 书库
    Library(
        route = AppRoutes.Library,
        label = "书库",
        selectedIcon = R.drawable.library_screen_filled,
        unselectedIcon = R.drawable.library_screen_outlined
    ),

    // 历史
    History(
        route = AppRoutes.History,
        label = "历史",
        selectedIcon = R.drawable.history_screen_filled,
        unselectedIcon = R.drawable.history_screen_outlined
    ),

    // 浏览
    Browse(
        route = AppRoutes.Browse,
        label = "浏览",
        selectedIcon = R.drawable.browse_screen_filled,
        unselectedIcon = R.drawable.browse_screen_outlined
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