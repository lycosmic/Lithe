package io.github.lycosmic.lithe.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import io.github.lycosmic.lithe.data.SettingsManager
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    settingsManager: SettingsManager,
    navViewModel: AppNavigationViewModel = hiltViewModel(),
) {
    val backStack = navViewModel.backStack

    NavDisplay(
        backStack = backStack,
        onBack = {
            navViewModel.pop()
        },
        modifier = modifier,
        entryProvider = { navKey ->
            when (navKey) {
                is AppRoutes.Library -> NavEntry(navKey) {
                    LibraryScreen(onGoToSettings = {
                        navViewModel.navigate(AppRoutes.Settings)
                    })
                }

                is AppRoutes.History -> NavEntry(navKey) {
                    HistoryScreen()
                }

                is AppRoutes.Browse -> NavEntry(navKey) {
                    BrowseScreen()
                }

                is AppRoutes.Settings -> NavEntry(navKey) {
                    SettingsScreen(settingsManager = settingsManager)
                }

                else -> error("Unknown navKey: $navKey")
            }
        }
    )
}

@Composable
fun LibraryScreen(modifier: Modifier = Modifier, onGoToSettings: () -> Unit) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Library"
        )

        Button(onClick = onGoToSettings) {
            Text(
                text = "Go to Settings",
            )
        }

    }
}

@Composable
fun HistoryScreen(modifier: Modifier = Modifier) {
    Text(
        text = "History",
        modifier = modifier
    )
}

@Composable
fun BrowseScreen(modifier: Modifier = Modifier) {
    Text(
        text = "Browse",
        modifier = modifier
    )
}

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
        Text(text = "Settings")
        Text(text = if (isDark) "Dark Mode" else "Light Mode")
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
