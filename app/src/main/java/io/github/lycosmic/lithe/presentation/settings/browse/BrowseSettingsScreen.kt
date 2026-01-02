package io.github.lycosmic.lithe.presentation.settings.browse

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.lycosmic.lithe.R
import io.github.lycosmic.lithe.presentation.settings.browse.components.DirectoryListItem
import io.github.lycosmic.lithe.presentation.settings.components.GridSizeSlider
import io.github.lycosmic.lithe.presentation.settings.components.InfoTip
import io.github.lycosmic.lithe.presentation.settings.components.SettingsGroupTitle
import io.github.lycosmic.lithe.presentation.settings.components.SettingsSubGroupTitle
import io.github.lycosmic.lithe.ui.components.LitheSegmentedButton
import io.github.lycosmic.lithe.util.extensions.labelResId
import io.github.lycosmic.model.Directory
import io.github.lycosmic.model.DisplayMode
import io.github.lycosmic.model.OptionItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseSettingsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BrowseSettingsViewModel = hiltViewModel()
) {

    val directories by viewModel.scannedDirectories.collectAsStateWithLifecycle()

    val displayMode by viewModel.displayMode.collectAsStateWithLifecycle()

    val gridColumnCount by viewModel.gridColumnCount.collectAsStateWithLifecycle()

    // 文件夹选择器
    val directoryPicker =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocumentTree()) { uri ->
            uri?.let {
                viewModel.onEvent(BrowseSettingEvent.DirectoryPicked(it))
            }
        }

    LaunchedEffect(
        Unit
    ) {
        viewModel.effects.collect { effect ->
            when (effect) {
                BrowseSettingEffect.OpenDirectoryPicker -> {
                    directoryPicker.launch(input = null)
                }
            }
        }
    }

    SettingBrowseScaffold(
        directories = directories,
        displayMode = displayMode,
        onDeleteDirectoryClicked = { directory ->
            viewModel.onEvent(BrowseSettingEvent.DeleteDirectoryClicked(directory))
        },
        onAddDirectoryClicked = {
            viewModel.onEvent(BrowseSettingEvent.AddDirectoryClicked)
        },
        onBackClicked = onNavigateBack,
        onDisplayModeChanged = { newMode ->
            viewModel.onEvent(BrowseSettingEvent.DisplayModeChanged(newMode))
        },
        modifier = modifier,
        gridColumnCount = gridColumnCount,
        onGridColumnCountChanged = { newCount ->
            viewModel.onEvent(BrowseSettingEvent.GridColumnCountChanged(newCount))
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingBrowseScaffold(
    directories: List<Directory>,
    displayMode: DisplayMode,
    onBackClicked: () -> Unit,
    gridColumnCount: Int,
    onGridColumnCountChanged: (Int) -> Unit,
    onDeleteDirectoryClicked: (Directory) -> Unit,
    onDisplayModeChanged: (DisplayMode) -> Unit,
    onAddDirectoryClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.browse)) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding(),
                )
        ) {
            // --- 扫描区域 ---
            item {
                SettingsGroupTitle(
                    title = { stringResource(R.string.scan) },
                    modifier = Modifier.padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 8.dp
                    )
                )
            }

            items(items = directories, key = { it.id }) { directory ->
                // 文件夹
                DirectoryListItem(
                    directory = directory,
                    onDeleteClick = {
                        onDeleteDirectoryClicked(directory)
                    }
                )
            }

            item {
                ListItem(
                    headlineContent = {
                        Text(text = stringResource(R.string.add_directory))
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.clickable {
                        onAddDirectoryClicked()
                    }
                )
            }

            // --- 提示 ---
            item {
                InfoTip(
                    message = stringResource(R.string.browse_settings_directories_message),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                )
            }

            // --- 分割线 ---
            item {
                HorizontalDivider()
            }

            // --- 显示 ---
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    SettingsGroupTitle(
                        title = { stringResource(R.string.display) },
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )

                    SettingsSubGroupTitle(
                        title = stringResource(R.string.display_mode),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    DisplayModeArea(
                        displayMode = displayMode,
                        gridColumnCount = gridColumnCount,
                        onDisplayModeChanged = onDisplayModeChanged,
                        onGridColumnCountChanged = onGridColumnCountChanged
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}


/**
 * 显示模式区域
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayModeArea(
    displayMode: DisplayMode,
    gridColumnCount: Int,
    onGridColumnCountChanged: (Int) -> Unit,
    onDisplayModeChanged: (DisplayMode) -> Unit,
) {
    LitheSegmentedButton(
        items = DisplayMode.entries.map { mode ->
            OptionItem(
                value = mode,
                label = stringResource(id = mode.labelResId),
                selected = mode == displayMode
            )
        }.toList(),
        onClick = { clickedMode ->
            onDisplayModeChanged(clickedMode)
        }
    )

    Spacer(modifier = Modifier.height(16.dp))

    AnimatedVisibility(
        visible = displayMode == DisplayMode.Grid,
        enter = slideInVertically(
            animationSpec = tween(),
            initialOffsetY = { -it / 2 }
        ) + fadeIn(),
        exit = slideOutVertically(
            animationSpec = tween(),
            targetOffsetY = { -it / 2 }
        ) + fadeOut(),
    ) {
        GridSizeSlider(
            value = gridColumnCount,
            onValueChange = {
                onGridColumnCountChanged(it)
            },
        )
    }
}
