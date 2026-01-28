package io.github.lycosmic.lithe.presentation.detail

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MoveUp
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.lycosmic.lithe.R
import io.github.lycosmic.lithe.presentation.detail.components.DeleteBookDialog
import io.github.lycosmic.lithe.presentation.detail.components.FileInfoBottomSheet
import io.github.lycosmic.lithe.ui.components.AsyncCoverImage
import io.github.lycosmic.lithe.ui.theme.LitheTheme
import io.github.lycosmic.lithe.util.FormatUtils
import io.github.lycosmic.lithe.util.ToastUtil
import my.nanihadesuka.compose.InternalLazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSelectionMode
import my.nanihadesuka.compose.ScrollbarSettings

@SuppressLint("LocalContextGetResourceValueCall")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    bookId: Long,
    onNavigateToLibrary: () -> Unit,
    onNavigateToReader: (bookId: Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BookDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = bookId) {
        viewModel.loadBook(bookId)
    }

    val book by viewModel.book.collectAsStateWithLifecycle()

    val filePath by viewModel.filePath.collectAsStateWithLifecycle()

    val progress by viewModel.progress.collectAsStateWithLifecycle()

    var bottomSheetVisible by remember { mutableStateOf(false) }

    // 控制移动书籍的对话框显隐
    var showMoveDialog by remember { mutableStateOf(false) }

    // 控制删除书籍的对话框显隐
    var showDeleteDialog by remember { mutableStateOf(false) }

    // 列表状态
    val scrollState = rememberLazyListState()

    val scrollbarSettings = ScrollbarSettings(
        thumbUnselectedColor = MaterialTheme.colorScheme.secondary,
        thumbSelectedColor = MaterialTheme.colorScheme.secondary.copy(0.8f),
        hideDelayMillis = 2000,
        durationAnimationMillis = 300,
        selectionMode = ScrollbarSelectionMode.Disabled,
        thumbThickness = 8.dp,
        scrollbarPadding = 4.dp
    )

    // 滚动行为
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    // 控制顶部栏的透明度
    val appBarAlpha by remember {
        derivedStateOf {
            // 完全不透明的滚动位置
            val targetOffset = 300f
            // 当前的滚动百分比
            val fraction = (scrollState.firstVisibleItemScrollOffset.toFloat() / targetOffset)
                .coerceIn(0f, 1f)
            fraction
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                BookDetailEffect.NavigateBack -> onNavigateToLibrary()
                BookDetailEffect.ShowDeleteBookDialog -> {
                    showDeleteDialog = true
                }

                BookDetailEffect.ShowMoveBookDialog -> {
                    showMoveDialog = true
                }

                BookDetailEffect.DismissDeleteBookDialog -> {
                    showDeleteDialog = false
                }

                BookDetailEffect.DismissMoveBookDialog -> {
                    showMoveDialog = false
                }

                is BookDetailEffect.ShowBookDeletedToast -> {
                    ToastUtil.show(effect.messageResId)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.surface.copy(
                        alpha = appBarAlpha
                    )
                ),
                title = {

                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            viewModel.onEvent(BookDetailEvent.OnBackClicked)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            bottomSheetVisible = !bottomSheetVisible
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = stringResource(id = R.string.file_info)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        Box(
            Modifier.padding(
                PaddingValues(bottom = innerPadding.calculateBottomPadding())
            )
        ) {
            // --- 模糊封面背景 ---
            book.coverPath?.let {
                val backgroundColor = MaterialTheme.colorScheme.surface

                AsyncCoverImage(
                    path = it,
                    animationDurationMillis = 300,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(innerPadding.calculateTopPadding() + 232.dp)
                        .drawWithContent {
                            drawContent()
                            drawRect(
                                brush = Brush.verticalGradient(
                                    0f to Color.Transparent,
                                    0.8f to backgroundColor
                                )
                            )
                        }
                        .blur(3.dp),
                    alpha = 0.4f
                )
            }

            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = innerPadding.calculateTopPadding()
                    )
            ) {
                item {
                    Row(
                        Modifier
                            .fillMaxWidth()
                    ) {
                        Box(
                            Modifier
                                .weight(1f)
                                .padding(end = 20.dp)
                                .aspectRatio(0.7f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    color = MaterialTheme.colorScheme.primaryContainer
                                )
                        ) {
                            if (book.coverPath != null) {
                                AsyncCoverImage(
                                    path = book.coverPath!!,
                                    contentDescription = null,
                                    modifier = Modifier.matchParentSize()
                                )
                            } else {
                                Image(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                                    modifier = Modifier.matchParentSize()
                                )
                            }
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .align(Alignment.CenterVertically),
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                text = book.title,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                            Row(modifier = Modifier.align(Alignment.Start)) {
                                Icon(
                                    imageVector = Icons.Outlined.Person, contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = book.author.joinToString(","),
                                    modifier = Modifier
                                        .padding(start = 4.dp)
                                        .align(Alignment.CenterVertically),
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Start,
                                )
                            }


                            Row(modifier = Modifier.align(Alignment.Start)) {
                                LinearProgressIndicator(
                                    progress = {
                                        progress
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(4.dp)
                                        .clip(MaterialTheme.shapes.small)
                                        .align(Alignment.CenterVertically),
                                    color = MaterialTheme.colorScheme.primary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                                )

                                Text(
                                    text = FormatUtils.formatProgress(progress),
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }


                    }
                }


                val buttonVerticalPadding = 16.dp
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        // --- 移动按钮 ---
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 8.dp,
                                        topEnd = 0.dp,
                                        bottomStart = 8.dp,
                                        bottomEnd = 0.dp
                                    )
                                )
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                                .clickable {
                                    viewModel.onEvent(BookDetailEvent.OnMoveClicked)
                                }
                                .padding(vertical = buttonVerticalPadding),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.MoveUp,
                                contentDescription = stringResource(id = R.string.move_book),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(id = R.string.move_book),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // --- 删除按钮 ---
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 0.dp,
                                        topEnd = 8.dp,
                                        bottomStart = 0.dp,
                                        bottomEnd = 8.dp
                                    )
                                )
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                                .clickable {
                                    viewModel.onEvent(BookDetailEvent.OnDeleteClicked)
                                }
                                .padding(vertical = buttonVerticalPadding),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = stringResource(id = R.string.delete_book_description),
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(id = R.string.delete_book_description),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }


                // 简介
                item {
                    Text(
                        text = book.description ?: stringResource(id = R.string.no_description),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                top = 16.dp
                            )
                    )
                }

                // 开始阅读按钮
                item {
                    Button(
                        onClick = {
                            onNavigateToReader(bookId)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text(text = stringResource(id = R.string.start_reading))
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // 滚动条
            InternalLazyColumnScrollbar(
                state = scrollState,
                settings = scrollbarSettings
            )
        }


        val lastOpenTime = if (book.lastReadTime == null) {
            stringResource(id = R.string.never_opened)
        } else {
            FormatUtils.formatDate(book.lastReadTime!!)
        }

        val size = FormatUtils.formatFileSize(book.fileSize)

        AnimatedVisibility(visible = bottomSheetVisible) {
            FileInfoBottomSheet(
                onDismissRequest = {
                    bottomSheetVisible = false
                },
                path = filePath.toString(),
                onEditPath = {},
                lastOpenTime = lastOpenTime,
                size = size
            )
        }

        if (showDeleteDialog) {
            DeleteBookDialog(
                onDismissRequest = { viewModel.onEvent(BookDetailEvent.OnDeleteDialogDismissed) },
                onConfirm = {
                    viewModel.onEvent(BookDetailEvent.OnConfirmDeleteClicked)
                },
                onCancel = {
                    viewModel.onEvent(BookDetailEvent.OnCancelDeleteClicked)
                }
            )
        }
    }
}

@Preview
@Composable
private fun BookDetailScreenPreview() {
    LitheTheme {
        BookDetailScreen(
            bookId = 1,
            onNavigateToLibrary = {},
            onNavigateToReader = {}
        )
    }
}
