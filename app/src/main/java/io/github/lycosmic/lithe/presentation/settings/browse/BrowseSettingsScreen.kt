package io.github.lycosmic.lithe.presentation.settings.browse

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: BrowseSettingsViewModel = hiltViewModel()
) {

    val directories by viewModel.scannedDirectories.collectAsStateWithLifecycle()

    // 文件夹选择器
    val directoryPicker =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocumentTree()) { uri ->
            uri?.let {
                viewModel.addDirectory(it)
            }
        }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("浏览") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
        val horizontalPadding = 16.dp
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = innerPadding.calculateBottomPadding(),
                    start = horizontalPadding,
                    end = horizontalPadding
                )
        ) {
            Text(text = "扫描", modifier = Modifier.padding(vertical = 8.dp))

            LazyColumn {
                items(items = directories, key = { it.id }) { directory ->

                    // 文件夹
                    ListItem(
                        headlineContent = {
                            Text(text = directory.path)  // 例: Download/Lithe
                        },
                        supportingContent = {
                            Text(text = directory.root) // 例: /storage/emulated/0/
                        },
                        leadingContent = {
                            Box(
                                modifier = Modifier
                                    .clip(shape = CircleShape)
                                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
                                    .padding(all = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Folder,
                                    contentDescription = null
                                )
                            }
                        },
                        trailingContent = {
                            IconButton(
                                onClick = {
                                    viewModel.removeDirectory(directory)
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = "移除文件夹"
                                )
                            }
                        }
                    )
                }
            }


            ListItem(
                headlineContent = {
                    Text(text = "添加文件夹")
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null
                    )
                },
                modifier = Modifier.clickable {
                    directoryPicker.launch(input = null)
                }
            )

            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = "提示",
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text(
                text = "这些文件夹用于扫描和检索浏览器中的文件。在此处删除文件夹并不会从库中删除实际文件或书籍。要撤销访问权限，请单击相应的按钮。",
                modifier = Modifier.padding(vertical = 8.dp)
            )

            HorizontalDivider()
        }
    }
}