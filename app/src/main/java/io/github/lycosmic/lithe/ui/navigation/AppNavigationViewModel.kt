package io.github.lycosmic.lithe.ui.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AppNavigationViewModel @Inject constructor() : ViewModel() {
    // 维护的返回栈
    val backStack = mutableStateListOf<AppRoutes>(AppRoutes.Library)

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