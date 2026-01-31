package io.github.lycosmic.lithe.presentation.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun SettingsItemWithSlider(
    title: String,
    subtitle: String? = null,
    subtitleResId: Int? = null,
    formatSubtitle: ((Float) -> Any)? = null,
    initialValue: Float,
    onSave: (Float) -> Unit,
    steps: Int,
    floatRange: ClosedFloatingPointRange<Float>,
    modifier: Modifier = Modifier,
) {
    // 节流处理
    var sliderValue by remember(initialValue) {
        mutableStateOf(initialValue)
    }

    val subtitle = subtitle
        ?: if (subtitleResId != null) {
            if (formatSubtitle != null) {
                stringResource(subtitleResId, formatSubtitle(sliderValue))
            } else {
                // 默认情况下转换为整数
                stringResource(subtitleResId, sliderValue.toInt())
            }
        } else {
            ""
        }

    Row(
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title)
            Text(
                text = subtitle, textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Slider(
            value = sliderValue,
            onValueChange = {
                sliderValue = it
            },
            onValueChangeFinished = {
                onSave(sliderValue)
            },
            steps = steps,
            valueRange = floatRange,
            colors = SliderDefaults.colors(
                activeTrackColor = MaterialTheme.colorScheme.secondary,
                thumbColor = MaterialTheme.colorScheme.primary,
                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                activeTickColor = MaterialTheme.colorScheme.onSecondary,
                inactiveTickColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}