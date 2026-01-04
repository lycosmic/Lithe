package io.github.lycosmic.lithe.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import io.github.lycosmic.lithe.presentation.browse.BrowseScreen
import io.github.lycosmic.lithe.presentation.detail.BookDetailScreen
import io.github.lycosmic.lithe.presentation.history.HistoryScreen
import io.github.lycosmic.lithe.presentation.library.LibraryScreen
import io.github.lycosmic.lithe.presentation.reader.ReaderScreen
import io.github.lycosmic.lithe.presentation.settings.SettingsScreen
import io.github.lycosmic.lithe.presentation.settings.appearance.AppearanceSettingsScreen
import io.github.lycosmic.lithe.presentation.settings.browse.BrowseSettingsScreen
import io.github.lycosmic.lithe.presentation.settings.general.GeneralSettingsScreen
import io.github.lycosmic.lithe.presentation.settings.library.LibrarySettingsScreen

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
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
                                onGoToBookRead = { bookId ->
                                    navViewModel.navigate(AppRoutes.Reader(bookId))
                                },
                                onGoToBookDetail = { bookId ->
                                    navViewModel.navigate(AppRoutes.BookDetail(bookId))
                                },
                                onGoToSettings = {
                                    navViewModel.navigate(AppRoutes.Settings)
                                },
                                onGoToHelp = {},
                                onGoToAbout = {},
                                onGoToBrowser = {
                                    navViewModel.navigate(AppRoutes.Browse)
                                },
                                onGoToLibrarySettings = {
                                    navViewModel.navigate(AppRoutes.SettingsLibrary)
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
                        onNavigateToGeneralSettings = {
                            navViewModel.navigate(AppRoutes.SettingsGeneral)
                        },
                        onNavigateToAppearanceSettings = {
                            navViewModel.navigate(AppRoutes.SettingsAppearance)
                        },
                        onNavigateToReaderSettings = {},
                        onNavigateToLibrarySettings = {
                            navViewModel.navigate(AppRoutes.SettingsLibrary)
                        },
                        onNavigateToBrowseSettings = {
                            navViewModel.navigate(AppRoutes.SettingsBrowse)
                        }
                    )

                    is AppRoutes.Reader -> ReaderScreen(
                        bookId = navKey.bookId,
                        navigateBack = {
                            navViewModel.pop()
                        }
                    )

                    is AppRoutes.SettingsAppearance -> {
                        AppearanceSettingsScreen(
                            onBackClick = {
                                navViewModel.pop()
                            }
                        )
                    }

                    is AppRoutes.SettingsBrowse -> BrowseSettingsScreen(
                        onNavigateBack = { navViewModel.pop() }
                    )

                    is AppRoutes.SettingsGeneral -> {
                        GeneralSettingsScreen(
                            onBackClick = {
                                navViewModel.pop()
                            }
                        )
                    }

                    is AppRoutes.SettingsLibrary -> {
                        LibrarySettingsScreen(
                            navigateBack = {
                                navViewModel.pop()
                            }
                        )
                    }

                    is AppRoutes.SettingsReader -> TODO()
                    is AppRoutes.BookDetail -> {
                        val bookId = navKey.bookId
                        BookDetailScreen(
                            bookId = bookId,
                            onNavigateToReader = {
                                navViewModel.navigate(AppRoutes.Reader(bookId))
                            },
                            onNavigateToLibrary = {
                                navViewModel.navigate(AppRoutes.Library)
                            }
                        )
                    }
                }
            }
        }
    )
}