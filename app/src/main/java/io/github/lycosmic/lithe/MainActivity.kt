package io.github.lycosmic.lithe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import io.github.lycosmic.lithe.data.SettingsManager
import io.github.lycosmic.lithe.ui.navigation.AppNavigation
import io.github.lycosmic.lithe.ui.theme.LitheTheme
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDark by
            settingsManager.isDarkMode.collectAsStateWithLifecycle(initialValue = false)

            LitheTheme(isDarkTheme = isDark) {
                Scaffold { paddingValues ->
                    AppNavigation(Modifier.padding(paddingValues), settingsManager)
                }
            }
        }
    }
}

