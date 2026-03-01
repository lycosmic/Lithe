package io.github.lycosmic.lithe.ui.navigation

import io.github.lycosmic.lithe.R
import io.github.lycosmic.lithe.presentation.navigation.AppRoutes

enum class AppTabs(
    val route: AppRoutes,
    val labelResId: Int,
    val selectedIcon: Int,
    val unselectedIcon: Int
) {
    // 书库
    Library(
        route = AppRoutes.Library,
        labelResId = R.string.library,
        selectedIcon = R.drawable.library_screen_filled,
        unselectedIcon = R.drawable.library_screen_outlined
    ),

    // 历史
    History(
        route = AppRoutes.History,
        labelResId = R.string.history,
        selectedIcon = R.drawable.history_screen_filled,
        unselectedIcon = R.drawable.history_screen_outlined
    ),

    // 浏览
    Browse(
        route = AppRoutes.Browse,
        labelResId = R.string.browse,
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