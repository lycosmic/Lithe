package io.github.lycosmic.lithe.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ChromeReaderMode
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.lycosmic.lithe.data.settings.SettingsManager
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToGeneralSettings: () -> Unit,
    onNavigateToAppearanceSettings: () -> Unit,
    onNavigateToReaderSettings: () -> Unit,
    onNavigateToLibrarySettings: () -> Unit,
    onNavigateToBrowseSettings: () -> Unit,
    settingsManager: SettingsManager,
    modifier: Modifier = Modifier,
) {
    // 是否是暗色模式
    val isDark by settingsManager.isDarkMode.collectAsState(initial = false)

    val scope = rememberCoroutineScope()

    // 创建滚动行为
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        // 将滚动事件传递给Scaffold
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(text = "设置")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding)
                .verticalScroll(state = rememberScrollState()),
        ) {
            SettingsItem(
                title = "常规",
                subTitle = "应用语言",
                icon = Icons.Outlined.Tune,
                onClick = onNavigateToGeneralSettings
            )
            SettingsItem(
                title = "外观",
                subTitle = "主题颜色",
                icon = Icons.Outlined.Palette,
                onClick = onNavigateToAppearanceSettings
            )
            SettingsItem(
                title = "阅读器",
                subTitle = "字体文本",
                icon = Icons.AutoMirrored.Outlined.ChromeReaderMode,
                iconContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                onClick = onNavigateToReaderSettings
            )
            SettingsItem(
                title = "书库",
                subTitle = "分类、显示、标签页",
                icon = Icons.AutoMirrored.Outlined.LibraryBooks,
                onClick = onNavigateToLibrarySettings
            )
            SettingsItem(
                title = "浏览",
                subTitle = "扫描、显示",
                icon = Icons.Outlined.FolderOpen,
                onClick = onNavigateToBrowseSettings
            )

            Switch(
                checked = isDark,
                onCheckedChange = { checked ->
                    scope.launch {
                        settingsManager.setDarkMode(checked)
                    }
                }
            )
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    iconContainerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    cornerRadius: Dp = 24.dp,
    verticalContentPadding: Dp = 8.dp, // 垂直内间距,注意: 组件基于内容撑开
    subTitle: String? = null,
    onClick: () -> Unit
) {
    ListItem(
        modifier = modifier
            .clip(
                shape = RoundedCornerShape(size = cornerRadius)
            )
            .background(color = MaterialTheme.colorScheme.surfaceContainerLow) // 卡片背景色
            .clickable {
                onClick()
            },
        headlineContent = {
            Text(text = title)
        },
        supportingContent = {
            Text(text = subTitle ?: "")
        },
        leadingContent = {
            Surface(
                shape = CircleShape, // 圆形图标
                color = iconContainerColor,
                modifier = Modifier
                    .padding(vertical = verticalContentPadding)
                    .size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
    )
}

@Preview
@Composable
fun SettingsItemPreview() {
    SettingsItem(
        title = "浏览",
        icon = Icons.Outlined.Explore,
        modifier = Modifier.fillMaxWidth(),
        iconContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        subTitle = "扫描、显示",
        onClick = {}
    )
}
