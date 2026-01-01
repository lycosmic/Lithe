package io.github.lycosmic.lithe.presentation.settings.appearance.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.lycosmic.lithe.R
import io.github.lycosmic.lithe.domain.model.Constants
import io.github.lycosmic.lithe.presentation.settings.components.SettingsSubGroupTitle
import io.github.lycosmic.lithe.ui.components.StyledText
import io.github.lycosmic.lithe.ui.theme.LitheTheme


/**
 * 颜色预设中的颜色行
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorChannelRow(
    label: String, // 标签
    value: Float, // 当前值，范围 0.0f ~ 1.0f
    initialValue: Float, // 初始值，范围 0.0f ~ 1.0f
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    // 颜色值
    val labelValue by remember(value) {
        derivedStateOf {
            "${(value * Constants.MAX_COLOR_VALUE).toInt()}"
        }
    }
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        // 标签和数值
        Column(
            modifier = Modifier.width(48.dp)
        ) {
            SettingsSubGroupTitle(
                title = label
            )

            StyledText(
                text = labelValue,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // 滑动条
        Slider(
            modifier = Modifier.weight(1f),
            valueRange = 0f..1f,
            value = value,
            onValueChange = {
                onValueChange(it)
            },
            interactionSource = interactionSource,
            thumb = {
                SliderDefaults.Thumb(
                    interactionSource = interactionSource
                )
            },
            colors = SliderDefaults.colors(
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                thumbColor = MaterialTheme.colorScheme.secondary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                activeTickColor = MaterialTheme.colorScheme.onSecondary,
                inactiveTickColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Spacer(modifier = Modifier.width(10.dp))

        // 历史记录图标
        IconButton(modifier = Modifier.size(48.dp), enabled = initialValue != value, onClick = {
            // 撤销
            onValueChange(initialValue)
        }) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = stringResource(id = R.string.undo),
                tint = if (initialValue == value) MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview
@Composable
fun ColorChannelRowPreview() {
    // 当前颜色
    var color by remember { mutableStateOf(Color(0xFFFAF8FF)) }
    // 颜色ULong值
    val initialValue = remember(Unit) { color.value }

    LitheTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(
                        Color(
                            (color.red * Constants.MAX_COLOR_VALUE).toInt(),
                            (color.green * Constants.MAX_COLOR_VALUE).toInt(),
                            (color.blue * Constants.MAX_COLOR_VALUE).toInt()
                        )
                    )
            )
            ColorChannelRow(
                label = stringResource(id = R.string.red),
                value = color.red,
                initialValue = Color(initialValue).red,
                onValueChange = {
                    color = color.copy(red = it)
                }
            )
        }
    }
}