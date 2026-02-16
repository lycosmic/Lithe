package io.github.lycosmic.lithe.presentation.history.cpmponents

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import io.github.lycosmic.lithe.R
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce


sealed class HistoryTopBarState {
    // 默认状态
    object Default : HistoryTopBarState()

    // 搜索状态
    object Search : HistoryTopBarState()
}


@OptIn(ExperimentalMaterial3Api::class, FlowPreview::class)
@Composable
fun HistoryTopBar(
    topBarState: HistoryTopBarState,
    initialSearchText: String,
    onSearchTextChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    onDeleteWholeHistoryClick: () -> Unit,
    onMoreClick: () -> Unit,
    onExitSearchClick: () -> Unit,
) {
    val localSearchText = remember(initialSearchText) { mutableStateOf(initialSearchText) }

    LaunchedEffect(localSearchText) {
        snapshotFlow { localSearchText.value }
            .debounce(100)
            .collectLatest {
                if (it == initialSearchText) {
                    return@collectLatest
                }

                onSearchTextChange(it)
            }
    }


    when (topBarState) {
        HistoryTopBarState.Default -> {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.history_title),
                    )
                },
                actions = {
                    // 搜索按钮
                    IconButton(
                        onClick = onSearchClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(id = R.string.search)
                        )
                    }

                    // 删除所有阅读历史记录
                    IconButton(
                        onClick = onDeleteWholeHistoryClick
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.DeleteSweep,
                            contentDescription = null
                        )
                    }

                    // 更多
                    IconButton(
                        onClick = onMoreClick
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null
                        )
                    }
                },
            )
        }

        HistoryTopBarState.Search -> {
            TopAppBar(
                title = {
                    TextField(
                        value = localSearchText.value,
                        onValueChange = onSearchTextChange,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = stringResource(R.string.search_hint),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.titleLarge,
                            )
                        },
                        singleLine = true,
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        )
                    )
                },
                navigationIcon = {
                    // 退出搜索
                    IconButton(onClick = onExitSearchClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.exit_search)
                        )
                    }
                },
                actions = {
                    // 更多
                    IconButton(onClick = onMoreClick) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.more),
                        )
                    }
                }
            )
        }
    }

}