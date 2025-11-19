package io.github.lycosmic.lithe.presentation.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseSettingsScreen(
    onNavigateBack: () -> Unit,
) {

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = { Text("浏览") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text("扫描")

            // 文件夹
            ListItem(
                headlineContent = {
                    Text(text = "文件路径")  // 例: Download/Lithe
                },
                supportingContent = {
                    Text(text = "存储路径") // 例: /storage/emulated/0/
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.Folder,
                        contentDescription = null
                    )
                },
                trailingContent = {
                    Icon(
                        imageVector = Icons.Filled.Clear,
                        contentDescription = "移除文件夹"
                    )
                }
            )

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
                    // TODO 添加文件夹
                }
            )
        }
    }
}