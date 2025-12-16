package io.github.lycosmic.lithe.presentation.detail

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import io.github.lycosmic.lithe.presentation.detail.components.DeleteBookDialog
import io.github.lycosmic.lithe.presentation.detail.components.FileInfoBottomSheet
import io.github.lycosmic.lithe.ui.theme.LitheTheme
import io.github.lycosmic.lithe.utils.FileUtils

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

    var bottomSheetVisible by remember { mutableStateOf(false) }

    // 控制移动书籍的对话框显隐
    var showMoveDialog by remember { mutableStateOf(false) }

    // 控制删除书籍的对话框显隐
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Snackbar 状态
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

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

                is BookDetailEffect.ShowBookDeletedSnackbar -> {
                    val messageText = context.getString(effect.messageResId)

                    snackbarHostState.showSnackbar(
                        message = messageText,
                        duration = SnackbarDuration.Short,
                        withDismissAction = true // 允许用户划掉
                    )
                }
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            TopAppBar(
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
                            contentDescription = "返回"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            bottomSheetVisible = !bottomSheetVisible
                        }
                    ) {
                        Icon(imageVector = Icons.Outlined.Info, contentDescription = "文件信息")
                    }
                },
            )
        },
        modifier = modifier
    ) { paddings ->
        Column(
            modifier = Modifier
                .padding(paddings)
                .padding(start = 16.dp, end = 16.dp, top = 16.dp)
        ) {
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
                        AsyncImage(
                            model = book.coverPath,
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
                            text = book.author,
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
                                book.progress
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
                            text = "${book.progress * 100}%",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }


            }


            val buttonVerticalPadding = 16.dp
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
                        contentDescription = "移动",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "移动",
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
                        contentDescription = "删除",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "删除",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }


            // 简介
            Text(
                text = book.description ?: "无简介", modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = 16.dp
                    )
            )

            // 开始阅读按钮
            Button(
                onClick = {
                    onNavigateToReader(bookId)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(text = "开始阅读")
            }
        }


        val lastOpenTime = if (book.lastReadTime == null) {
            "永不"
        } else {
            FileUtils.formatDate(book.lastReadTime!!)
        }

        val size = FileUtils.formatFileSize(book.fileSize)

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
