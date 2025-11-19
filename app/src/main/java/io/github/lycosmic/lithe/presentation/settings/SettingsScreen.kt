package io.github.lycosmic.lithe.presentation.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.lycosmic.lithe.data.settings.SettingsManager
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
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

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
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
                windowInsets = WindowInsets()
            )
        },
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
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
    cornerRadius: Dp = 12.dp,
    subTitle: String? = null,
    onClick: () -> Unit
) {
    ListItem(
        modifier = modifier
            .clip(
                shape = RoundedCornerShape(size = cornerRadius)
            )
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
            IconButton(
                onClick = {},
                colors = IconButtonDefaults.iconButtonColors().copy(
                    containerColor = iconContainerColor
                )
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null
                )
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
