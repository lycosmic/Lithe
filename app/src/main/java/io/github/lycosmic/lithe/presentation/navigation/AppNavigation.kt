package io.github.lycosmic.lithe.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import io.github.lycosmic.lithe.data.SettingsManager
import io.github.lycosmic.lithe.presentation.browse.BrowseScreen
import io.github.lycosmic.lithe.presentation.history.HistoryScreen
import io.github.lycosmic.lithe.presentation.library.LibraryScreen
import io.github.lycosmic.lithe.presentation.settings.SettingsScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    settingsManager: SettingsManager,
    navViewModel: AppNavigationViewModel,
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
                    LibraryScreen(
                        onGoToSettings = {
                            navViewModel.navigate(AppRoutes.Settings)
                        }
                    )
                }

                is AppRoutes.History -> NavEntry(navKey) {
                    HistoryScreen(
                        onGoToSettings = {
                            navViewModel.navigate(AppRoutes.Settings)
                        }
                    )
                }

                is AppRoutes.Browse -> NavEntry(navKey) {
                    BrowseScreen(
                        onGoToSettings = {
                            navViewModel.navigate(AppRoutes.Settings)
                        }
                    )
                }

                is AppRoutes.Settings -> NavEntry(navKey) {
                    SettingsScreen(settingsManager = settingsManager)
                }

                else -> error("Unknown navKey: $navKey")
            }
        }
    )
}