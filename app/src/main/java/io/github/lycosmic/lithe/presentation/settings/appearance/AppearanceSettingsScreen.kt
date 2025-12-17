package io.github.lycosmic.lithe.presentation.settings.appearance

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.lycosmic.lithe.R
import io.github.lycosmic.lithe.data.model.OptionItem
import io.github.lycosmic.lithe.data.model.ThemeMode
import io.github.lycosmic.lithe.ui.components.LitheSegmentedButton
import io.github.lycosmic.lithe.ui.theme.LitheTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceSettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AppearanceSettingsViewModel = hiltViewModel()
) {
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle(initialValue = ThemeMode.SYSTEM)

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
                .padding(horizontal = 16.dp)
        ) {

            Text(
                text = stringResource(R.string.theme_preference),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // --- 暗黑主题
            Text(
                text = stringResource(R.string.dark_theme),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
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