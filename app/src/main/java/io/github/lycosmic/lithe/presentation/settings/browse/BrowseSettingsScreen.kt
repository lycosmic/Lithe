package io.github.lycosmic.lithe.presentation.settings.browse

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.lycosmic.lithe.R
import io.github.lycosmic.lithe.data.model.DisplayMode
import io.github.lycosmic.lithe.data.model.OptionItem
import io.github.lycosmic.lithe.data.model.ScannedDirectory
import io.github.lycosmic.lithe.ui.components.LitheSegmentedButton

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
    directories: List<ScannedDirectory>,
    displayMode: DisplayMode,
    onBackClicked: () -> Unit,
    gridColumnCount: Int,
    onGridColumnCountChanged: (Int) -> Unit,
    onDeleteDirectoryClicked: (ScannedDirectory) -> Unit,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding(),
                )
        ) {
            // --- 扫描 ---
            ScanArea(
                directories = directories,
                onAddDirectoryClicked = onAddDirectoryClicked,
                onDeleteDirectoryClicked = onDeleteDirectoryClicked,
            )

            // --- 提示 ---
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = stringResource(R.string.tip),
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.browse_settings_directories_message),
                    style = MaterialTheme.typography.bodySmall,
                )
            }


            Spacer(modifier = Modifier.height(24.dp))

            // --- 分割线 ---
            HorizontalDivider()

            Spacer(modifier = Modifier.height(16.dp))

            // --- 显示 ---
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = stringResource(R.string.display),
                    modifier = Modifier.padding(vertical = 8.dp),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.primary
                    )
                )

                DisplayModeArea(
                    displayMode = displayMode,
                    gridColumnCount = gridColumnCount,
                    onDisplayModeChanged = onDisplayModeChanged,
                    onGridColumnCountChanged = onGridColumnCountChanged
                )
            }
        }
    }
}

/**
 * 扫描区域
 */
@Composable
fun ScanArea(
    directories: List<ScannedDirectory>,
    onDeleteDirectoryClicked: (ScannedDirectory) -> Unit,
    onAddDirectoryClicked: () -> Unit,
) {
    Text(
        text = stringResource(R.string.scan),
        modifier = Modifier.padding(top = 24.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
        style = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.primary
        )
    )

    AnimatedVisibility(
        visible = directories.isNotEmpty(),
    ) {
        LazyColumn {
            items(items = directories, key = { it.id }) { directory ->

                // 文件夹
                ListItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem(
                            fadeInSpec = spring(stiffness = Spring.StiffnessMedium),
                            placementSpec = spring(stiffness = Spring.StiffnessLow),
                            fadeOutSpec = spring(stiffness = Spring.StiffnessMedium)
                        ),
                    headlineContent = {
                        Text(text = directory.path)
                    },
                    supportingContent = {
                        Text(text = directory.root)
                    },
                    leadingContent = {
                        Box(
                            modifier = Modifier
                                .clip(shape = CircleShape)
                                .background(color = MaterialTheme.colorScheme.surfaceContainer)
                                .padding(all = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Folder,
                                contentDescription = null
                            )
                        }
                    },
                    trailingContent = {
                        IconButton(
                            onClick = {
                                onDeleteDirectoryClicked(directory)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Clear,
                                contentDescription = stringResource(R.string.remove_directory)
                            )
                        }
                    }
                )

            }
        }
    }

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


/**
 * 显示模式区域
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayModeArea(
    displayMode: DisplayMode,
    gridColumnCount: Int,
    valueRange: IntRange = 0..10,
    onGridColumnCountChanged: (Int) -> Unit,
    onDisplayModeChanged: (DisplayMode) -> Unit,
) {

    val tipText = if (gridColumnCount == valueRange.first) {
        stringResource(R.string.auto)
    } else {
        stringResource(R.string.column_count, gridColumnCount)
    }

    Text(
        text = stringResource(R.string.display_mode)
    )

    Spacer(modifier = Modifier.height(8.dp))

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
        Row {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(R.string.grid_size))

                Text(text = tipText, textAlign = TextAlign.Center)
            }

            Spacer(modifier = Modifier.width(8.dp))

            Slider(
                value = gridColumnCount.toFloat(),
                onValueChange = {
                    onGridColumnCountChanged(it.toInt())
                },
                steps = valueRange.last,
                valueRange = valueRange.first.toFloat().rangeTo(valueRange.last.toFloat()),
                colors = SliderDefaults.colors(
                    activeTrackColor = MaterialTheme.colorScheme.secondary,
                    thumbColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                    activeTickColor = MaterialTheme.colorScheme.onSecondary,
                    inactiveTickColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
