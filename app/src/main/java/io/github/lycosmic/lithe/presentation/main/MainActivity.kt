package io.github.lycosmic.lithe.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import io.github.lycosmic.lithe.data.SettingsManager
import io.github.lycosmic.lithe.presentation.navigation.AppNavigation
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

            LaunchedEffect(Unit) {
                // TODO 模拟延迟，等待设置中心初始化完成
                delay(500)
                isSettingsReady = true
            }

            LitheTheme(isDarkTheme = isDark) {
                Scaffold { paddingValues ->
                    AppNavigation(Modifier.Companion.padding(paddingValues), settingsManager)
                }
            }
        }
    }
}