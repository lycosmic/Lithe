package io.github.lycosmic.lithe.presentation.main

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.res.painterResource
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import io.github.lycosmic.data.settings.AppThemeOption
import io.github.lycosmic.data.settings.ScreenOrientation
import io.github.lycosmic.data.settings.SettingsManager
import io.github.lycosmic.data.settings.ThemeMode
import io.github.lycosmic.lithe.log.logI
import io.github.lycosmic.lithe.presentation.navigation.AppNavigation
import io.github.lycosmic.lithe.presentation.navigation.AppNavigationViewModel
import io.github.lycosmic.lithe.ui.navigation.AppTabs
import io.github.lycosmic.lithe.ui.theme.LitheTheme
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {

        var isSettingsReady by mutableStateOf(false)

        // 安装启动屏
        installSplashScreen().setKeepOnScreenCondition {
            !isSettingsReady
        }

        super.onCreate(savedInstanceState)
        logI {
            "MainActivity 初始化成功"
        }
        enableEdgeToEdge()
        setContent {
            val themeMode by
            settingsManager.themeMode.collectAsStateWithLifecycle(initialValue = ThemeMode.SYSTEM)

            val isDark = when (themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

            val appTheme by settingsManager.appTheme.collectAsStateWithLifecycle(
                initialValue = AppThemeOption.MERCURY
            )

            val showNavigationBarLabels by settingsManager.showNavigationBarLabels.collectAsStateWithLifecycle(
                initialValue = true
            )

            // 监听屏幕方向设置
            val screenOrientation by settingsManager.screenOrientation.collectAsStateWithLifecycle(
                initialValue = ScreenOrientation.DEFAULT
            )

            val navViewModel = hiltViewModel<AppNavigationViewModel>()

            LaunchedEffect(Unit) {
                // TODO 模拟延迟，等待设置中心初始化完成
                delay(130)
                isSettingsReady = true
            }

            // 应用屏幕方向
            LaunchedEffect(screenOrientation) {
                val activityOrientation = when (screenOrientation) {
                    ScreenOrientation.DEFAULT -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                    ScreenOrientation.FREE -> ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
                    ScreenOrientation.PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    ScreenOrientation.LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    ScreenOrientation.PORTRAIT_LOCKED -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    ScreenOrientation.LANDSCAPE_LOCKED -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
                requestedOrientation = activityOrientation
            }

            // 监听当前路由,决定了高亮标签和底部栏是否显示
            val currentRoute = navViewModel.currentRoute

            val currentTab = AppTabs.getTabByRoute(currentRoute.value)

            val showBottomBar = currentTab != null

            LitheTheme(appTheme = appTheme, isDarkTheme = isDark) {
                Scaffold(
                    bottomBar = {
                        AnimatedVisibility(
                            visible = showBottomBar,
                            enter = slideInVertically { it } + expandVertically(),
                            exit = slideOutVertically { it } + shrinkVertically()
                        ) {
                            // 导航栏
                            NavigationBar {
                                AppTabs.entries.forEach { tab ->
                                    val isSelected = currentTab == tab
                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = { navViewModel.switchBottomTab(tab) },
                                        icon = {
                                            val iconId =
                                                if (isSelected) tab.selectedIcon else tab.unselectedIcon

                                            Icon(
                                                painter = painterResource(id = iconId),
                                                contentDescription = tab.label
                                            )
                                        },
                                        label = {
                                            if (showNavigationBarLabels) {
                                                Text(tab.label)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { paddingValues ->
                    AppNavigation(
                        modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()), // 防止底部栏遮挡内容
                        navViewModel = navViewModel
                    )
                }
            }
        }
    }
}