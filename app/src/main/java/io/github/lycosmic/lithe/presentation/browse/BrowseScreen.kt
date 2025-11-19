package io.github.lycosmic.lithe.presentation.browse

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.github.lycosmic.lithe.ui.components.ActionItem
import io.github.lycosmic.lithe.ui.components.LitheActionSheet

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BrowseScreen(
    modifier: Modifier = Modifier,
    onGoToSettings: () -> Unit,
    onGoToAbout: () -> Unit,
    onGoToHelp: () -> Unit
) {
    // 控制底部抽屉是否显示
    var showBottomSheet by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "浏览",
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            showBottomSheet = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "更多"
                        )
                    }
                },
                windowInsets = WindowInsets()
            )
        }
    ) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "这里是书浏览列表"
            )
        }

        LitheActionSheet(
            showBottomSheet = showBottomSheet,
            onDismissRequest = {
                showBottomSheet = false
            },
            groups = listOf(
                listOf(
                    ActionItem(
                        text = "关于",
                        icon = null,
                        isDestructive = false,
                        onClick = {
                            showBottomSheet = false
                            onGoToAbout()
                        }
                    ),
                    ActionItem(
                        text = "帮助",
                        icon = null,
                        isDestructive = false,
                        onClick = {
                            showBottomSheet = false
                            onGoToHelp()
                        }
                    )
                ),
                listOf(
                    ActionItem(
                        text = "设置",
                        icon = null,
                        isDestructive = false,
                        onClick = {
                            showBottomSheet = false
                            onGoToSettings()
                        },
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                )
            )
        )
    }
}
