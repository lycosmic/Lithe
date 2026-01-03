package io.github.lycosmic.lithe.presentation.settings.library

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.lycosmic.domain.model.BookTitlePosition
import io.github.lycosmic.domain.model.Category
import io.github.lycosmic.domain.model.DisplayMode
import io.github.lycosmic.domain.model.OptionItem
import io.github.lycosmic.lithe.R
import io.github.lycosmic.lithe.presentation.settings.components.GridSizeSlider
import io.github.lycosmic.lithe.presentation.settings.components.InfoTip
import io.github.lycosmic.lithe.presentation.settings.components.SelectionChip
import io.github.lycosmic.lithe.presentation.settings.components.SettingsGroupTitle
import io.github.lycosmic.lithe.presentation.settings.components.SettingsItemWithSwitch
import io.github.lycosmic.lithe.presentation.settings.components.SettingsSubGroupTitle
import io.github.lycosmic.lithe.presentation.settings.library.components.CategoryList
import io.github.lycosmic.lithe.presentation.settings.library.components.CreateCategoryDialog
import io.github.lycosmic.lithe.presentation.settings.library.components.DeleteCategoryDialog
import io.github.lycosmic.lithe.presentation.settings.library.components.UpdateCategoryDialog
import io.github.lycosmic.lithe.ui.components.LitheSegmentedButton
import io.github.lycosmic.lithe.util.extensions.labelResId
import io.github.lycosmic.lithe.util.toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibrarySettingsScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LibrarySettingsViewModel = hiltViewModel()
) {
    // 书籍显示模式
    val bookDisplayMode by viewModel.bookDisplayMode.collectAsStateWithLifecycle()

    // 网格列数
    val bookGridColumnCount by viewModel.bookGridColumnCount.collectAsStateWithLifecycle()

    // 书籍标题位置
    val bookTitlePosition by viewModel.bookTitlePosition.collectAsStateWithLifecycle()

    // 显示阅读按钮
    val showReadButton by viewModel.showReadButton.collectAsStateWithLifecycle()

    // 显示阅读进度
    val showReadProgress by viewModel.showReadProgress.collectAsStateWithLifecycle()

    // 显示分类标签
    val showCategoryTab by viewModel.showCategoryTab.collectAsStateWithLifecycle()

    // 显示分类标签的默认分类
    val alwaysShowDefaultCategoryTab by viewModel.alwaysShowDefaultCategoryTab.collectAsStateWithLifecycle()

    // 显示书籍数量
    val showBookCount by viewModel.showBookCount.collectAsStateWithLifecycle()

    // 每个分类有不同的排序方式
    val eachCategoryHasDifferentSort by viewModel.eachCategoryHasDifferentSort.collectAsStateWithLifecycle()

    // 创建分类对话框可见性
    var createCategoryDialogVisible by remember { mutableStateOf(false) }

    // 编辑分类对话框可见性
    var updateCategoryDialogVisible by remember { mutableStateOf<Category?>(null) }

    // 删除分类对话框可见性
    var deleteCategoryDialogVisible by remember { mutableStateOf<Long?>(null) }

    val categories by viewModel.categoryList.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                LibrarySettingsEffect.NavigateBack -> {
                    navigateBack()
                }

                LibrarySettingsEffect.OpenCreateCategoryDialog -> {
                    createCategoryDialogVisible = true
                }

                LibrarySettingsEffect.CategoryNameExists -> {
                    R.string.category_exists.toast()
                }

                is LibrarySettingsEffect.OpenDeleteCategoryDialog -> {
                    deleteCategoryDialogVisible = effect.categoryId
                }

                is LibrarySettingsEffect.OpenEditCategoryDialog -> {
                    updateCategoryDialogVisible = effect.category
                }
            }
        }
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
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

            // --- 分类列表 ---
            CategoryList(
                categories = categories,
                onEditClick = {
                    viewModel.onEvent(LibrarySettingsEvent.OnEditCategoryClick(it))
                },
                onDeleteClick = {
                    viewModel.onEvent(LibrarySettingsEvent.OnDeleteCategoryClick(it.id))
                }
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
                    stringResource(R.string.display)
                },
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
            )
            SettingsSubGroupTitle(
                title = stringResource(R.string.display_mode),
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
            )

            // --- 网格或列表 ---
            LitheSegmentedButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                items = DisplayMode.entries.map { displayMode ->
                    OptionItem(
                        value = displayMode,
                        label = stringResource(displayMode.labelResId),
                        selected = displayMode == bookDisplayMode
                    )
                }
            ) { libraryDisplayMode ->
                viewModel.onEvent(LibrarySettingsEvent.OnBookDisplayModeChange(libraryDisplayMode))
            }

            // --- 网格大小和标题位置
            AnimatedVisibility(
                visible = bookDisplayMode == DisplayMode.Grid,
                enter = slideInVertically(
                    animationSpec = tween(),
                    initialOffsetY = { -it / 2 }
                ) + fadeIn(),
                exit = slideOutVertically(
                    animationSpec = tween(),
                    targetOffsetY = { -it / 2 }
                ) + fadeOut(),
            ) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))

                    GridSizeSlider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        value = bookGridColumnCount,
                        onValueChange = {
                            viewModel.onEvent(LibrarySettingsEvent.OnBookGridColumnCountChange(it))
                        },
                    )

                    SettingsSubGroupTitle(
                        title = stringResource(R.string.title_position),
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BookTitlePosition.entries.forEach { titlePosition ->
                            SelectionChip(
                                text = stringResource(titlePosition.labelResId),
                                isSelected = bookTitlePosition == titlePosition,
                            ) {
                                // 点击了标题位置标签
                                viewModel.onEvent(
                                    LibrarySettingsEvent.OnBookTitlePositionChange(
                                        titlePosition
                                    )
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // --- 显示阅读按钮 ---
            SettingsItemWithSwitch(
                title = stringResource(R.string.show_read_button),
                checked = showReadButton,
                isDividerVisible = true,
                onCheckedChange = {
                    viewModel.onEvent(LibrarySettingsEvent.OnBookShowReadButtonChange(it))
                }
            )

            // --- 显示进度 ---
            SettingsItemWithSwitch(
                title = stringResource(R.string.show_progress),
                checked = showReadProgress,
                isDividerVisible = true,
                onCheckedChange = {
                    viewModel.onEvent(LibrarySettingsEvent.OnBookShowProgressChange(it))
                },
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider()

            // --- 标签页 ---
            SettingsGroupTitle(
                title = {
                    stringResource(R.string.tab_title)
                },
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 16.dp, end = 16.dp)
            )

            SettingsItemWithSwitch(
                title = stringResource(R.string.show_category_tab),
                checked = showCategoryTab,
                isDividerVisible = true,
                onCheckedChange = {
                    viewModel.onEvent(LibrarySettingsEvent.OnCategoryTabVisibleChange(it))
                },
            )

            SettingsItemWithSwitch(
                title = stringResource(R.string.always_show_default_tab),
                checked = alwaysShowDefaultCategoryTab,
                isDividerVisible = true,
                onCheckedChange = {
                    viewModel.onEvent(LibrarySettingsEvent.OnDefaultCategoryTabVisibleChange(it))
                },
            )

            SettingsItemWithSwitch(
                title = stringResource(R.string.show_book_count),
                checked = showBookCount,
                isDividerVisible = true,
                onCheckedChange = {
                    viewModel.onEvent(LibrarySettingsEvent.OnCategoryBookCountVisibleChange(it))
                },
            )

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider()

            // --- 排序 ---
            SettingsGroupTitle(
                title = {
                    stringResource(R.string.sort)
                },
                modifier = Modifier.padding(16.dp)
            )

            SettingsItemWithSwitch(
                title = stringResource(R.string.different_sort_per_category),
                checked = eachCategoryHasDifferentSort,
                isDividerVisible = true,
                onCheckedChange = {
                    viewModel.onEvent(LibrarySettingsEvent.OnCategorySortDifferentChange(it))
                },
            )

            InfoTip(
                message = stringResource(R.string.sort_info_message),
                modifier = Modifier.padding(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (createCategoryDialogVisible) {
            CreateCategoryDialog(
                onDismissRequest = {
                    createCategoryDialogVisible = false
                },
                onConfirm = {
                    createCategoryDialogVisible = false
                    viewModel.onEvent(LibrarySettingsEvent.OnCreateCategory(it))
                }
            )
        }

        updateCategoryDialogVisible?.let {
            UpdateCategoryDialog(
                initialText = it.name,
                onDismissRequest = {
                    updateCategoryDialogVisible = null // 置空即关闭
                },
                onConfirm = { newName ->
                    viewModel.onEvent(LibrarySettingsEvent.OnUpdateCategory(it.copy(name = newName)))
                    updateCategoryDialogVisible = null
                }
            )
        }

        deleteCategoryDialogVisible?.let { categoryId ->
            DeleteCategoryDialog(
                onDismissRequest = {
                    deleteCategoryDialogVisible = null
                },
                onConfirm = {
                    viewModel.onEvent(LibrarySettingsEvent.OnDeleteCategory(categoryId))
                    deleteCategoryDialogVisible = null
                }
            )
        }

    } // End of Scaffold

}

