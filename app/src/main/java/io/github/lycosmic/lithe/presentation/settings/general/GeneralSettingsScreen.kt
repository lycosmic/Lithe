package io.github.lycosmic.lithe.presentation.settings.general

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import io.github.lycosmic.lithe.presentation.settings.components.SelectionChip
import io.github.lycosmic.lithe.presentation.settings.components.SettingsItemWithSwitch
import io.github.lycosmic.lithe.presentation.settings.components.SettingsSubGroupTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralSettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GeneralSettingsViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    LaunchedEffect(Unit) {
        viewModel.effects.collect {
            when (it) {
                is GeneralSettingsEffect.NavigateBack -> onBackClick()
            }
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        stringResource(R.string.general),
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddings ->
        Column(
            modifier = Modifier
                .padding(paddings)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // 允许页面滚动
                .padding(horizontal = 16.dp)
        ) {
            SettingsSubGroupTitle(
                title = stringResource(R.string.app_language),
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // 使用 FlowRow 实现自动换行布局
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                AppLanguage.SUPPORTED_LANGUAGES.forEach { language ->
                    SelectionChip(
                        text = language.name,
                        isSelected = language.code == uiState.languageCode,
                        onClick = {
                            viewModel.onEvent(GeneralSettingsEvent.OnLanguageClick(language.code))
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 双击返回退出部分
            SettingsItemWithSwitch(
                title = stringResource(R.string.double_tap_to_exit),
                subtitle = stringResource(R.string.double_tap_to_exit_message),
                checked = uiState.isDoubleBackToExitEnabled,
                onCheckedChange = {
                    viewModel.onEvent(GeneralSettingsEvent.OnDoubleClickExitClick(it))
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

