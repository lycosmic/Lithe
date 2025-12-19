package io.github.lycosmic.lithe.presentation.settings.appearance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.lycosmic.lithe.R
import io.github.lycosmic.lithe.data.model.AppThemeOption
import io.github.lycosmic.lithe.data.model.OptionItem
import io.github.lycosmic.lithe.data.model.ThemeMode
import io.github.lycosmic.lithe.presentation.settings.appearance.components.ThemePreviewItem
import io.github.lycosmic.lithe.presentation.settings.components.SettingsGroupTitle
import io.github.lycosmic.lithe.presentation.settings.components.SettingsSubGroupTitle
import io.github.lycosmic.lithe.ui.components.LitheSegmentedButton
import io.github.lycosmic.lithe.ui.theme.LitheTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceSettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AppearanceSettingsViewModel = hiltViewModel()
) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()

    val currentThemeId by
    viewModel.appThemeId.collectAsStateWithLifecycle()

    val isNavLabelVisible by
    viewModel.isNavLabelVisible.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()


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

                Switch(
                    checked = isNavLabelVisible,
                    onCheckedChange = {
                        viewModel.onEvent(
                            AppearanceSettingsEvent.OnNavLabelVisibleChange(it)
                        )
                    }
                )
            }

        }
    }
}

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


@Preview
@Composable
private fun AppearanceSettingsScreenPreview() {
    LitheTheme {
        AppearanceSettingsScreen(onBackClick = {})
    }
}