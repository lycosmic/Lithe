package io.github.lycosmic.lithe.presentation.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.lycosmic.lithe.utils.FileUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    bookId: Long,
    onNavigateToLibrary: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BookDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = bookId) {
        viewModel.loadBook(bookId)
    }

    val book by viewModel.book.collectAsStateWithLifecycle()

    val filePath by viewModel.filePath.collectAsStateWithLifecycle()

    var bottomSheetVisible by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                BookDetailEffect.NavigateBack -> onNavigateToLibrary()
            }
        }
    }

    Scaffold(
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
                }
            )
        },
        modifier = modifier
    ) { paddings ->
        Column(
            modifier = Modifier
                .padding(paddings)
                .padding(horizontal = 16.dp)
        ) {
            //


            // 移动, 删除

            // 开始阅读按钮
            Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                Text(text = "开始阅读")
            }
        }


        val lastOpenTime = if (book?.lastReadTime == null) {
            "永不"
        } else {
            FileUtils.formatDate(book?.lastReadTime!!)
        }

        val size = FileUtils.formatFileSize(book?.fileSize ?: 0L)

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
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FileInfoBottomSheet(
    onDismissRequest: () -> Unit,
    path: String,
    onEditPath: (String) -> Unit,
    lastOpenTime: String,
    size: String,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    ModalBottomSheet(
        modifier = modifier
            .fillMaxWidth(),
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Column(
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 24.dp
            )
        ) {
            Text(
                text = "文件信息",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 路径
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                ReadOnlyInfoField(
                    label = "路径",
                    value = path,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                IconButton(onClick = { onEditPath(path) }) {
                    Icon(
                        imageVector = Icons.Outlined.EditNote,
                        contentDescription = "编辑路径",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 上次打开
            ReadOnlyInfoField(
                label = "上次打开",
                value = lastOpenTime,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 文件大小
            ReadOnlyInfoField(
                label = "文件大小",
                value = size,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun ReadOnlyInfoField(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        label = { Text(text = label) },
        modifier = modifier.fillMaxWidth(),
        textStyle = MaterialTheme.typography.bodyLarge,
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.outline,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        )
    )
}