package io.github.lycosmic.lithe.presentation.settings.appearance

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.rounded.DragHandle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.lycosmic.lithe.R
import io.github.lycosmic.lithe.data.model.AppThemeOption
import io.github.lycosmic.lithe.data.model.OptionItem
import io.github.lycosmic.lithe.data.model.ThemeMode
import io.github.lycosmic.lithe.domain.model.ColorPreset
import io.github.lycosmic.lithe.extension.logV
import io.github.lycosmic.lithe.presentation.settings.appearance.components.ColorPresetEditorCard
import io.github.lycosmic.lithe.presentation.settings.appearance.components.ThemePreviewItem
import io.github.lycosmic.lithe.presentation.settings.components.SettingsGroupTitle
import io.github.lycosmic.lithe.presentation.settings.components.SettingsSubGroupTitle
import io.github.lycosmic.lithe.presentation.settings.components.SettingsSwitch
import io.github.lycosmic.lithe.ui.components.LitheSegmentedButton
import io.github.lycosmic.lithe.ui.components.StyledText
import io.github.lycosmic.lithe.ui.theme.LitheTheme
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceSettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AppearanceSettingsViewModel = hiltViewModel()
) {
    // 当前主题模式
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    // 当前主题
    val currentThemeId by
    viewModel.appThemeId.collectAsStateWithLifecycle()

    // 导航栏标签可见性
    val isNavLabelVisible by
    viewModel.isNavLabelVisible.collectAsStateWithLifecycle()

    // 是否启用快速更改颜色预设
    val isQuickChangeColorPresetEnabled by
    viewModel.quickChangeColorPreset.collectAsStateWithLifecycle()

    // 滚动行为，用于实现 AppBar 的折叠效果
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    // 颜色预设
    val presetsListFromDb by viewModel.presets.collectAsStateWithLifecycle()
    // 本地维护的颜色预设
    val localPresets = remember { mutableStateListOf<ColorPreset>() }
    LaunchedEffect(presetsListFromDb) {
        // 同步数据库中的颜色预设
        localPresets.clear()
        localPresets.addAll(presetsListFromDb)
    }

    // 当前的颜色预设
    val selectedPreset by viewModel.currentPreset.collectAsStateWithLifecycle()

    // 可拖拽的列表状态
    val lazyListState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(lazyListState) { from, to ->
        logV {
            "改变颜色预设顺序，from: $from, to: $to"
        }
        // 更新列表顺序
        localPresets.apply {
            add(to.index, removeAt(from.index))
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect {
            when (it) {
                is AppearanceSettingsEffect.NavigateBack -> onBackClick()
            }
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.appearance),
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.onEvent(AppearanceSettingsEvent.OnBackClick)
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) { paddings ->
        Column(
            Modifier
                .padding(paddings)
                .verticalScroll(rememberScrollState())
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                SettingsGroupTitle(
                    title = {
                        stringResource(R.string.theme_preference)
                    },
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                // --- 暗黑主题
                SettingsSubGroupTitle(
                    title = stringResource(R.string.dark_theme),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                LitheSegmentedButton(
                    items = listOf(
                        OptionItem(
                            label = stringResource(R.string.system),
                            value = ThemeMode.SYSTEM,
                            selected = themeMode == ThemeMode.SYSTEM
                        ),
                        OptionItem(
                            label = stringResource(R.string.disabled),
                            value = ThemeMode.LIGHT,
                            selected = themeMode == ThemeMode.LIGHT
                        ),
                        OptionItem(
                            label = stringResource(R.string.enabled),
                            value = ThemeMode.DARK,
                            selected = themeMode == ThemeMode.DARK
                        )
                    ),
                    onClick = { mode ->
                        viewModel.onEvent(AppearanceSettingsEvent.OnThemeModeClick(mode))
                    }
                )

                // --- 应用主题
                SettingsSubGroupTitle(
                    title = stringResource(R.string.app_theme),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            ThemeSelector(
                currentThemeId = currentThemeId,
                onThemeSelected = { themeId ->
                    viewModel.onEvent(AppearanceSettingsEvent.OnAppThemeChange(themeId))
                }
            )

            // --- 显示导航标签 ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.show_navigation_labels),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = stringResource(R.string.show_navigation_labels_description),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                SettingsSwitch(
                    checked = isNavLabelVisible,
                    onCheckedChange = {
                        viewModel.onEvent(
                            AppearanceSettingsEvent.OnNavLabelVisibleChange(it)
                        )
                    }
                )
            }

            HorizontalDivider()

            // --- 颜色预设 ---
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                SettingsGroupTitle(
                    title = {
                        stringResource(R.string.color)
                    },
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )

                SettingsSubGroupTitle(
                    title = stringResource(R.string.color_presets),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    state = lazyListState,
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(items = localPresets, key = { it.id }) { preset ->
                        // 可排序的列表项
                        ReorderableItem(
                            state = reorderableLazyListState,
                            key = preset.id
                        ) { isDragging ->
                            // 拖拽项的阴影高度
                            val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp)

                            ColorPresetOption(
                                colorPreset = preset,
                                onClick = {
                                    // 点击了颜色预设
                                    viewModel.onEvent(
                                        AppearanceSettingsEvent.OnColorPresetClick(
                                            preset
                                        )
                                    )
                                },
                                dragEnabled = preset.isSelected && localPresets.size > 1,
                                elevation = elevation,
                                onDragStopped = {
                                    viewModel.onEvent(
                                        AppearanceSettingsEvent.OnColorPresetDragStopped(
                                            localPresets
                                        )
                                    )
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- 颜色预设编辑器 ---
                ColorPresetEditorCard(
                    colorPreset = selectedPreset ?: ColorPreset.defaultColorPreset,
                    onColorPresetNameChange = { colorPreset, newName ->
                        viewModel.onEvent(
                            AppearanceSettingsEvent.OnColorPresetNameChange(colorPreset, newName)
                        )
                    },
                    onDelete = {
                        viewModel.onEvent(
                            AppearanceSettingsEvent.OnDeleteColorPresetClick(it)
                        )
                    },
                    onShuffle = {
                        viewModel.onEvent(AppearanceSettingsEvent.OnShuffleColorPresetClick(it))
                    },
                    onCreateNew = {
                        viewModel.onEvent(AppearanceSettingsEvent.OnAddColorPresetClick)
                    },
                    onBgColorChange = { preset, newBgColor ->
                        viewModel.onEvent(
                            AppearanceSettingsEvent.OnColorPresetBgColorChange(
                                preset,
                                newBgColor
                            )
                        )
                    },
                    onTextColorChange = { preset, newTextColor ->
                        viewModel.onEvent(
                            AppearanceSettingsEvent.OnColorPresetTextColorChange(
                                preset,
                                newTextColor
                            )
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.quick_color_preset_change),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = stringResource(R.string.swipe_to_change_color_preset),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    SettingsSwitch(
                        checked = isQuickChangeColorPresetEnabled,
                        onCheckedChange = {
                            viewModel.onEvent(
                                AppearanceSettingsEvent.OnQuickChangeColorPresetEnabledChange(it)
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

        }
    }
}

/**
 * 应用主题选择器
 */
@Composable
private fun ThemeSelector(currentThemeId: String, onThemeSelected: (String) -> Unit) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items = AppThemeOption.entries, key = { it.id }) { themeOption ->
            ThemePreviewItem(
                appTheme = themeOption,
                isSelected = themeOption.id == currentThemeId,
                onClick = { onThemeSelected(themeOption.id) }
            )
        }
    }
}

/**
 * 颜色预设标签
 */
@Composable
fun ReorderableCollectionItemScope.ColorPresetOption(
    modifier: Modifier = Modifier,
    colorPreset: ColorPreset,
    onClick: () -> Unit,
    dragEnabled: Boolean,
    onDragStopped: () -> Unit, // 拖拽结束
    elevation: Dp = 0.dp
) {
    val title = colorPreset.name.ifBlank {
        stringResource(R.string.color_preset_query, colorPreset.id.toString())
    }

    // 边框颜色
    val borderColor = if (colorPreset.isSelected) {
        colorPreset.textColor
    } else {
        colorPreset.textColor.copy(alpha = 0.3f)
    }

    Row(
        modifier = modifier
            .clip(CircleShape)
            .shadow(elevation)
            .border(width = 2.dp, color = borderColor, shape = CircleShape)
            .padding(2.dp)
            .background(colorPreset.backgroundColor, CircleShape)
            .clickable(enabled = !colorPreset.isSelected) {
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 选中图标
        AnimatedVisibility(
            visible = colorPreset.isSelected,
            enter = expandHorizontally() + fadeIn(),
            exit = shrinkHorizontally() + fadeOut()
        ) {
            Icon(
                imageVector = Icons.Default.Done,
                contentDescription = stringResource(id = R.string.selected_content_desc),
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(18.dp),
                tint = colorPreset.textColor
            )
        }

        // 预设名称
        StyledText(
            text = title.trim(),
            style = MaterialTheme.typography.labelLarge.copy(
                color = colorPreset.textColor
            ),
            maxLines = 1
        )

        // 拖动手柄
        AnimatedVisibility(
            visible = dragEnabled,
            enter = expandHorizontally() + fadeIn(),
            exit = shrinkHorizontally() + fadeOut()
        ) {
            Icon(
                imageVector = Icons.Rounded.DragHandle,
                contentDescription = stringResource(id = R.string.drag_content_desc),
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(18.dp)
                    .draggableHandle(
                        onDragStarted = {

                        },
                        onDragStopped = onDragStopped,
                    ),
                tint = colorPreset.textColor
            )
        }
    }

}


@Preview
@Composable
private fun AppearanceSettingsScreenPreview() {
    LitheTheme {

    }
}