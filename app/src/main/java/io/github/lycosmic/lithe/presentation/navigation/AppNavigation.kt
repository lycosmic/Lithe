package io.github.lycosmic.lithe.presentation.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import io.github.lycosmic.lithe.data.settings.SettingsManager
import io.github.lycosmic.lithe.presentation.browse.BrowseScreen
import io.github.lycosmic.lithe.presentation.history.HistoryScreen
import io.github.lycosmic.lithe.presentation.library.LibraryScreen
import io.github.lycosmic.lithe.presentation.settings.SettingsScreen
import io.github.lycosmic.lithe.presentation.settings.browse.BrowseSettingsScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    settingsManager: SettingsManager,
    navViewModel: AppNavigationViewModel,
) {
    val backStack = navViewModel.backStack

    // 状态持有者, 用于保存页面状态
    val saveableStateHolder = rememberSaveableStateHolder()

    NavDisplay(
        backStack = backStack,
        onBack = {
            navViewModel.pop()
        },
        modifier = modifier,
        entryProvider = { navKey ->
            NavEntry(navKey) {
                when (navKey) {
                    is AppRoutes.Library ->
                        // 只要key相同,就会恢复之前保存的状态,比如滚动位置
                        // 注意: 对于State状态需要使用rememberSaveable, 而不是使用remember
                        saveableStateHolder.SaveableStateProvider(key = navKey.toString()) {
                            LibraryScreen(
                                onGoToBookRead = {

                                },
                                onGoToBookDetail = {},
                                onGoToSettings = {
                                    navViewModel.navigate(AppRoutes.Settings)
                                },
                                onGoToHelp = {},
                                onGoToAbout = {},
                                onGoToBrowser = {
                                    navViewModel.navigate(AppRoutes.Browse)
                                }
                            )
                        }

                    is AppRoutes.History ->
                        saveableStateHolder.SaveableStateProvider(key = navKey.toString()) {
                            HistoryScreen(
                                onGoToSettings = {
                                    navViewModel.navigate(AppRoutes.Settings)
                                },
                                onGoToHelp = {},
                                onGoToAbout = {}
                            )
                        }

                    is AppRoutes.Browse ->
                        saveableStateHolder.SaveableStateProvider(key = navKey.toString()) {
                            BrowseScreen(
                                onGoToSettings = {
                                    navViewModel.navigate(AppRoutes.Settings)
                                },
                                onNavigateToBrowseSettings = {
                                    navViewModel.navigate(AppRoutes.SettingsBrowse)
                                },
                                onGoToHelp = {},
                                onGoToAbout = {}
                            )
                        }

                    is AppRoutes.Settings -> SettingsScreen(
                        onNavigateBack = {
                            navViewModel.pop()
                        },
                        onNavigateToGeneralSettings = {},
                        onNavigateToAppearanceSettings = {},
                        onNavigateToReaderSettings = {},
                        onNavigateToLibrarySettings = {},
                        onNavigateToBrowseSettings = {
                            navViewModel.navigate(AppRoutes.SettingsBrowse)
                        },
                        settingsManager = settingsManager
                    )

                    is AppRoutes.Reader -> Text(text = "Reade")
                    is AppRoutes.SettingsAppearance -> TODO()
                    is AppRoutes.SettingsBrowse -> BrowseSettingsScreen(
                        onNavigateBack = { navViewModel.pop() }
                    )

                    is AppRoutes.SettingsGeneral -> TODO()
                    is AppRoutes.SettingsLibrary -> TODO()
                    is AppRoutes.SettingsReader -> TODO()
                }
            }
        }
    )
}