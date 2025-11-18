package io.github.lycosmic.lithe.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.lycosmic.lithe.data.SettingsManager
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(settingsManager: SettingsManager, modifier: Modifier = Modifier) {
    // 是否是暗色模式
    val isDark by settingsManager.isDarkMode.collectAsState(initial = false)

    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "设置页面")
        Text(text = if (isDark) "暗黑模式" else "浅色模式")
        Switch(
            checked = isDark,
            onCheckedChange = { checked ->
                scope.launch {
                    settingsManager.setDarkMode(checked)
                }
            }
        )
    }
}
