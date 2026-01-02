package io.github.lycosmic.lithe.presentation.settings.appearance.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.lycosmic.lithe.R
import io.github.lycosmic.lithe.presentation.settings.components.SettingsSubGroupTitle
import io.github.lycosmic.lithe.ui.components.StyledText
import io.github.lycosmic.lithe.ui.theme.LitheTheme
import io.github.lycosmic.lithe.util.extensions.backgroundColor
import io.github.lycosmic.lithe.util.extensions.textColor
import io.github.lycosmic.model.ColorPreset
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce

/**
 * 颜色预设编辑卡片
 */
@OptIn(FlowPreview::class)
@Composable
fun ColorPresetEditorCard(
    colorPreset: ColorPreset, // 颜色预设
    onColorPresetNameChange: (ColorPreset, String) -> Unit, // 颜色预设名称改变
    onBgColorChange: (ColorPreset, colorArgb: Int) -> Unit, // 背景颜色改变
    onTextColorChange: (ColorPreset, colorArgb: Int) -> Unit, // 文本颜色改变
    onDelete: (ColorPreset) -> Unit, // 删除颜色预设
    onShuffle: (ColorPreset) -> Unit, // 打乱颜色预设
    onCreateNew: () -> Unit, // 创建颜色预设
    modifier: Modifier = Modifier
) {
    // 颜色初始值
    val initialBgColorValue = remember(colorPreset.id) { colorPreset.backgroundColor.value }
    // 颜色值
    var localBgColor by remember(colorPreset.backgroundColor) { mutableStateOf(colorPreset.backgroundColor) }

    // 背景颜色
    val initialTextColorValue = remember(colorPreset.id) { colorPreset.textColor.value }
    var localTextColor by remember(colorPreset.textColor) { mutableStateOf(colorPreset.textColor) }


    LaunchedEffect(localBgColor) {
        snapshotFlow {
            localBgColor
        }.debounce(50).collectLatest {
            onBgColorChange(colorPreset, it.toArgb())
        }
    }

    LaunchedEffect(localTextColor) {
        snapshotFlow {
            localTextColor
        }.debounce(50).collectLatest {
            onTextColorChange(colorPreset, it.toArgb())
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(horizontal = 18.dp)
    ) {
        // --- 卡片头部 ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 标题输入框
            BasicTextField(
                value = colorPreset.name,
                singleLine = true,
                modifier = Modifier.weight(1f),
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = MaterialTheme.typography.titleLarge.fontSize,
                    lineHeight = MaterialTheme.typography.titleLarge.lineHeight,
                    fontFamily = MaterialTheme.typography.titleLarge.fontFamily
                ),
                onValueChange = {
                    onColorPresetNameChange(colorPreset, it)
                },
                keyboardOptions = KeyboardOptions(
                    KeyboardCapitalization.Sentences
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant)
            ) { innerText ->
                Box(
                    modifier = Modifier.fillMaxHeight(),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (colorPreset.name.isEmpty()) {
                        StyledText(
                            text = stringResource(id = R.string.color_preset_editor_title),
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            maxLines = 1
                        )
                    }
                    innerText()
                }
            }

            // 操作图标组
            Row {
                IconButton(onClick = { onDelete(colorPreset) }) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = {
                    onShuffle(colorPreset)
                }) {
                    Icon(
                        Icons.Default.Shuffle,
                        null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onCreateNew) {
                    Icon(
                        Icons.Default.Add,
                        null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 背景颜色 ---
        SettingsSubGroupTitle(
            title = stringResource(id = R.string.color_preset_editor_background_color),
        )

        Spacer(modifier = Modifier.height(8.dp))

        ColorChannelRow(
            label = stringResource(R.string.red),
            value = localBgColor.red,
            initialValue = Color(initialBgColorValue).red,
            onValueChange = {
                localBgColor = localBgColor.copy(red = it)
            }
        )
        ColorChannelRow(
            label = stringResource(R.string.green),
            value = localBgColor.green,
            initialValue = Color(initialBgColorValue).green,
            onValueChange = {
                localBgColor = localBgColor.copy(green = it)
            }
        )
        ColorChannelRow(
            label = stringResource(R.string.blue),
            value = localBgColor.blue,
            initialValue = Color(initialBgColorValue).blue,
            onValueChange = {
                localBgColor = localBgColor.copy(blue = it)
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- 字体颜色 ---
        SettingsSubGroupTitle(
            title = stringResource(id = R.string.color_preset_editor_text_color),
        )

        Spacer(modifier = Modifier.height(8.dp))

        ColorChannelRow(
            label = stringResource(R.string.red),
            value = localTextColor.red,
            initialValue = Color(initialTextColorValue).red,
            onValueChange = {
                localTextColor = localTextColor.copy(red = it)
            }
        )
        ColorChannelRow(
            label = stringResource(R.string.green),
            value = localTextColor.green,
            initialValue = Color(initialTextColorValue).green,
            onValueChange = {
                localTextColor = localTextColor.copy(green = it)
            }
        )
        ColorChannelRow(
            label = stringResource(R.string.blue),
            value = localTextColor.blue,
            initialValue = Color(initialTextColorValue).blue,
            onValueChange = {
                localTextColor = localTextColor.copy(blue = it)
            }
        )
    }

}


@Preview
@Composable
private fun ColorPresetEditorCardPreview() {
    LitheTheme {

    }
}