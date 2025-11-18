package io.github.lycosmic.lithe.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import io.github.lycosmic.lithe.data.SettingsManager
import io.github.lycosmic.lithe.presentation.navigation.AppNavigation
import io.github.lycosmic.lithe.presentation.navigation.AppNavigationViewModel
import io.github.lycosmic.lithe.ui.navigation.AppTabs
import io.github.lycosmic.lithe.ui.theme.LitheTheme
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {

        var isSettingsReady by mutableStateOf(false)

        // 安装启动屏
        installSplashScreen().setKeepOnScreenCondition {
            !isSettingsReady
        }

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDark by
            settingsManager.isDarkMode.collectAsStateWithLifecycle(initialValue = false)

            val navViewModel = hiltViewModel<AppNavigationViewModel>()

            LaunchedEffect(Unit) {
                // TODO 模拟延迟，等待设置中心初始化完成
                delay(500)
                isSettingsReady = true
            }

            // 监听当前路由,决定了高亮标签和底部栏是否显示
            val currentRoute = navViewModel.currentRoute

            val currentTab = AppTabs.getTabByRoute(currentRoute.value)

            val showBottomBar = currentTab != null

            LitheTheme(isDarkTheme = isDark) {
                Scaffold(
                    bottomBar = {
                        AnimatedVisibility(
                            visible = showBottomBar,
                            enter = slideInVertically { it } + expandVertically(),
                            exit = slideOutVertically { it } + shrinkVertically()
                        ) {
                            NavigationBar {
                                AppTabs.entries.forEach { tab ->
                                    val isSelected = currentTab == tab
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = { navViewModel.switchBottomTab(tab) },
                                        icon = {
                                            Icon(
                                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                                contentDescription = tab.label
                                            )
                                        },
                                        label = { Text(tab.label) }
                                    )
                                }
                            }
                        }
                    }
                ) { paddingValues ->
                    AppNavigation(
                        modifier = Modifier.Companion.padding(paddingValues), // 防止底部栏遮挡内容
                        settingsManager = settingsManager,
                        navViewModel = navViewModel
                    )
                }
            }
        }
    }
}