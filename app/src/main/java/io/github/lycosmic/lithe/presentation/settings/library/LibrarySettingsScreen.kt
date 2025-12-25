package io.github.lycosmic.lithe.presentation.settings.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import io.github.lycosmic.lithe.R
import io.github.lycosmic.lithe.data.model.LibraryDisplayMode
import io.github.lycosmic.lithe.data.model.OptionItem
import io.github.lycosmic.lithe.data.model.TitlePosition
import io.github.lycosmic.lithe.presentation.settings.components.InfoTip
import io.github.lycosmic.lithe.presentation.settings.components.SelectionChip
import io.github.lycosmic.lithe.presentation.settings.components.SettingsGroupTitle
import io.github.lycosmic.lithe.presentation.settings.components.SettingsItemWithSwitch
import io.github.lycosmic.lithe.presentation.settings.components.SettingsSubGroupTitle
import io.github.lycosmic.lithe.ui.components.LitheSegmentedButton

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
                .verticalScroll(rememberScrollState())
        ) {
            // --- 分类 ---
            SettingsGroupTitle(
                title = {
                    stringResource(R.string.category)
                },
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp)
            )

            // --- 创建分类 ---
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

            // --- 提示 ---
            InfoTip(
                modifier = Modifier.padding(16.dp),
                message = stringResource(R.string.category_info_message)
            )

            HorizontalDivider()

            // --- 显示模式 ---
            SettingsGroupTitle(
                title = {
                    "显示"
                },
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
            )
            SettingsSubGroupTitle(
                title = "显示模式",
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            )

            // --- 网格或列表 ---
            LitheSegmentedButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                items = LibraryDisplayMode.entries.map { displayMode ->
                    OptionItem(
                        value = displayMode,
                        label = stringResource(displayMode.labelResId),
                        selected = true
                    )
                }
            ) { libraryDisplayMode ->

            }

            // --- 网格大小 ---

            SettingsSubGroupTitle(
                title = "标题位置",
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TitlePosition.entries.forEach { titlePosition ->
                    SelectionChip(
                        text = stringResource(titlePosition.labelResId),
                        isSelected = true,
                    ) {
                        // 点击了标题位置标签
                    }
                }
            }


            // --- 显示阅读按钮 ---
            SettingsItemWithSwitch(
                title = "显示“阅读”按钮",
                checked = true,
                isDividerVisible = true,
                onCheckedChange = {

                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // --- 显示进度 ---
            SettingsItemWithSwitch(
                title = "显示进度",
                checked = true,
                isDividerVisible = true,
                onCheckedChange = {

                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            HorizontalDivider()

            // --- 标签页 ---
            SettingsGroupTitle(
                title = {
                    "标签页"
                },
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
            )

            SettingsItemWithSwitch(
                title = "显示分类标签页",
                checked = true,
                isDividerVisible = true,
                onCheckedChange = {

                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            SettingsItemWithSwitch(
                title = "始终显示默认标签页",
                checked = true,
                isDividerVisible = true,
                onCheckedChange = {

                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            SettingsItemWithSwitch(
                title = "显示书籍数",
                checked = true,
                isDividerVisible = true,
                onCheckedChange = {

                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )


            HorizontalDivider()

            // --- 排序 ---
            SettingsGroupTitle(
                title = {
                    "排序"
                },
                modifier = Modifier.padding(16.dp)
            )

            SettingsItemWithSwitch(
                title = "每个分类不同排序依据",
                checked = true,
                isDividerVisible = true,
                onCheckedChange = {

                },
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}