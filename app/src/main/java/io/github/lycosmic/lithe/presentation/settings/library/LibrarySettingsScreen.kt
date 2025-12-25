package io.github.lycosmic.lithe.presentation.settings.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import io.github.lycosmic.lithe.R
import io.github.lycosmic.lithe.presentation.settings.components.InfoTip
import io.github.lycosmic.lithe.presentation.settings.components.SettingsGroupTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibrarySettingsScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibrarySettingsViewModel = hiltViewModel()
) {

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                LibrarySettingsEffect.NavigateBack -> {
                    navigateBack()
                }
            }
        }
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.library_settings))
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.onEvent(LibrarySettingsEvent.OnBackClick)
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddings ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddings)
        ) {
            SettingsGroupTitle(
                title = {
                    stringResource(R.string.category)
                },
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)
            )

            ListItem(
                headlineContent = {
                    Text(text = stringResource(R.string.create_category))
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null
                    )
                },
                modifier = Modifier.clickable {
                    viewModel.onEvent(LibrarySettingsEvent.OnCreateCategoryClick)
                }
            )

            InfoTip(
                modifier = Modifier.padding(16.dp),
                message = stringResource(R.string.category_info_message)
            )

            HorizontalDivider()
        }
    }
}