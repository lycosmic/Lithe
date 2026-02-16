package io.github.lycosmic.lithe.presentation.history

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.lycosmic.lithe.R
import io.github.lycosmic.lithe.presentation.history.cpmponents.HistoryDeleteWholeHistoryDialog
import io.github.lycosmic.lithe.presentation.history.cpmponents.HistoryItem
import io.github.lycosmic.lithe.presentation.history.cpmponents.HistoryTopBar
import io.github.lycosmic.lithe.presentation.history.cpmponents.HistoryTopBarState
import io.github.lycosmic.lithe.presentation.history.model.HistoryTimeLabel
import io.github.lycosmic.lithe.presentation.settings.components.SettingsSubGroupTitle
import io.github.lycosmic.lithe.ui.components.ActionItem
import io.github.lycosmic.lithe.ui.components.LitheActionSheet
import io.github.lycosmic.lithe.ui.components.StyledText
import my.nanihadesuka.compose.InternalLazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSelectionMode
import my.nanihadesuka.compose.ScrollbarSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = hiltViewModel(),
    onGoToSettings: () -> Unit,
    navigateBack: () -> Unit,
    navigateToBookReading: (bookId: Long) -> Unit,
    navigateToBookDetail: (bookId: Long) -> Unit
) {
    var showMoreBottomSheet by remember { mutableStateOf(false) }

    val topBarState by viewModel.topBarState.collectAsStateWithLifecycle()

    val searchText by viewModel.searchText.collectAsStateWithLifecycle()

    val groupedHistories by viewModel.groupedHistories.collectAsStateWithLifecycle()

    val historyIsEmpty = remember(groupedHistories) {
        groupedHistories.isEmpty()
    }

    val historyListState = rememberLazyListState()

    BackHandler {
        // 处于搜索模式，则返回到默认模式
        if (topBarState == HistoryTopBarState.Search) {
            viewModel.onEvent(HistoryEvent.OnExitSearchModeClick)
            return@BackHandler
        } else {
            navigateBack()
        }
    }


    var deleteWholeHistoryDialogVisible by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is HistoryEffect.NavigateToBookReading -> {
                    navigateToBookReading(effect.bookId)
                }

                HistoryEffect.OnDeleteWholeHistoryDialogDismiss -> {
                    deleteWholeHistoryDialogVisible = false
                }

                HistoryEffect.OnDeleteWholeHistoryDialogShow -> {
                    deleteWholeHistoryDialogVisible = true
                }

                HistoryEffect.OnMoreBottomSheetDismiss -> {
                    showMoreBottomSheet = false
                }

                HistoryEffect.OnMoreBottomSheetShow -> {
                    showMoreBottomSheet = true
                }

                is HistoryEffect.NavigateToBookDetail -> {
                    navigateToBookDetail(effect.bookId)
                }

                HistoryEffect.NavigateToSettings -> {
                    onGoToSettings()
                }
            }
        }
    }

    val primaryScrollbar = ScrollbarSettings(
        thumbUnselectedColor = MaterialTheme.colorScheme.secondary,
        thumbSelectedColor = MaterialTheme.colorScheme.secondary.copy(0.8f),
        hideDelayMillis = 2000,
        durationAnimationMillis = 300,
        selectionMode = ScrollbarSelectionMode.Disabled,
        thumbThickness = 8.dp,
        scrollbarPadding = 4.dp
    )


    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            HistoryTopBar(
                topBarState = topBarState,
                initialSearchText = searchText,
                onSearchTextChange = {
                    viewModel.onEvent(HistoryEvent.OnSearchTextChange(it))
                },
                onSearchClick = {
                    viewModel.onEvent(HistoryEvent.OnSearchClick)
                },
                onDeleteWholeHistoryClick = {
                    viewModel.onEvent(HistoryEvent.OnDeleteWholeHistoryClick)
                },
                onMoreClick = {
                    viewModel.onEvent(HistoryEvent.OnMoreClick)
                },
                onExitSearchClick = {
                    viewModel.onEvent(HistoryEvent.OnExitSearchModeClick)
                }
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
        ) {
            AnimatedVisibility(visible = !historyIsEmpty) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = historyListState,
                    userScrollEnabled = true,
                    verticalArrangement = Arrangement.Top,
                ) {
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    groupedHistories.forEachIndexed { index, groupedHistory ->
                        item {
                            if (index > 0) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            SettingsSubGroupTitle(
                                modifier = Modifier
                                    .animateItem()
                                    .padding(horizontal = 16.dp),
                                title = when (groupedHistory.label) {
                                    is HistoryTimeLabel.Other -> {
                                        groupedHistory.label.date
                                    }

                                    HistoryTimeLabel.Today -> {
                                        stringResource(R.string.today)
                                    }

                                    HistoryTimeLabel.Yesterday -> {
                                        stringResource(R.string.yesterday)
                                    }
                                },
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        items(
                            groupedHistory.histories,
                            key = { it.id }
                        ) { history ->
                            HistoryItem(
                                history = history,
                                onBodyClick = {
                                    // 跳转到书籍详情页
                                    viewModel.onEvent(HistoryEvent.OnHistoryClick(bookId = history.book.id))
                                },
                                onTitleClick = {
                                    // 跳转到阅读页
                                    viewModel.onEvent(
                                        HistoryEvent.OnHistoryTitleClick(
                                            bookId = history.book.id
                                        )
                                    )
                                },
                                onDeleteClick = {
                                    // 删除阅读历史记录
                                    viewModel.onEvent(
                                        HistoryEvent.OnDeleteHistoryClick(
                                            history = history
                                        )
                                    )
                                }
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                InternalLazyColumnScrollbar(
                    state = historyListState,
                    settings = primaryScrollbar
                )
            }

            // 占位符
            HistoryEmptyPlaceholder(
                isVisible = historyIsEmpty,
            )
        }

        if (deleteWholeHistoryDialogVisible) {
            HistoryDeleteWholeHistoryDialog(
                onDismissRequest = {
                    viewModel.onEvent(HistoryEvent.OnDeleteWholeHistoryDialogDismiss)
                },
                onConfirm = {
                    viewModel.onEvent(HistoryEvent.OnDeleteWholeHistoryDialogConfirm)
                }
            )
        }
        LitheActionSheet(
            showBottomSheet = showMoreBottomSheet,
            onDismissRequest = {
                viewModel.onEvent(HistoryEvent.OnMoreBottomSheetDismiss)
            },
            groups = listOf(
                listOf(
                    ActionItem(
                        text = stringResource(id = R.string.about),
                        icon = null,
                        isDestructive = false,
                        onClick = {
                            viewModel.onEvent(HistoryEvent.OnAboutClick)
                        }
                    ),
                    ActionItem(
                        text = stringResource(id = R.string.help),
                        icon = null,
                        isDestructive = false,
                        onClick = {
                            viewModel.onEvent(HistoryEvent.OnHelpClick)
                        }
                    )
                ),
                listOf(
                    ActionItem(
                        text = stringResource(id = R.string.settings),
                        icon = null,
                        isDestructive = false,
                        onClick = {
                            viewModel.onEvent(HistoryEvent.OnSettingsClick)
                        },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
            )
        )
    }
}


@Composable
fun BoxScope.HistoryEmptyPlaceholder(isVisible: Boolean, modifier: Modifier = Modifier) {
    AnimatedVisibility(
        visible = isVisible,
        modifier = modifier.align(Alignment.Center),
        enter = fadeIn(tween(300)),
        exit = fadeOut(tween(0))
    ) {
        Column(
            Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 80.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.empty_history),
                contentDescription = null,
                modifier = Modifier.size(150.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            StyledText(
                text = stringResource(id = R.string.history_empty_placeholder_text),
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            )
        }

    }
}