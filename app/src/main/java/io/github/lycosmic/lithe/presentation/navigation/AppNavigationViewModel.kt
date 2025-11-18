package io.github.lycosmic.lithe.presentation.navigation

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.lycosmic.lithe.ui.navigation.AppTabs
import javax.inject.Inject

@HiltViewModel
class AppNavigationViewModel @Inject constructor() : ViewModel() {
    // 维护的返回栈
    val backStack = mutableStateListOf<AppRoutes>(AppRoutes.defaultRoute)

    // 获取当前路由,以便显示高亮
    val currentRoute = derivedStateOf {
        backStack.lastOrNull()
    }

    /**
     * 切换底部标签
     */
    fun switchBottomTab(tab: AppTabs) {
        // 如果已在当前页,不做任何事
        if (currentRoute.value == tab.route) {
            return
        }

        // 清空返回栈
        backStack.clear()
        // 添加新的路由
        backStack.add(tab.route)
    }


    /**
     * 导航
     */
    fun navigate(route: AppRoutes) {
        backStack.add(route)
    }

    /**
     * 返回
     */
    fun pop() {
        if (backStack.size > 1) {
            backStack.removeLastOrNull()
        }
    }
}