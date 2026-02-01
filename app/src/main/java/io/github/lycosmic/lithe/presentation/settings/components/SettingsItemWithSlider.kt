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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.lycosmic.lithe.ui.theme.LitheTheme
import kotlin.math.roundToInt

@Composable
fun SettingsItemWithIntSlider(
    title: String,
    modifier: Modifier = Modifier,
    subtitleResId: Int? = null,
    initialValue: Int,
    onSave: (Int) -> Unit,
    valueRange: IntRange,
    steps: Int? = null
) {
    // 节流处理
    var sliderValue by remember(initialValue) {
        mutableIntStateOf(initialValue)
    }

    val computedSteps = steps ?: (valueRange.last - valueRange.first - 1).coerceAtLeast(0)

    Row(
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            val displaySubtitle = subtitleResId?.let {
                stringResource(it, sliderValue)
            } ?: sliderValue.toString()

            Text(
                text = displaySubtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Slider(
            value = sliderValue.toFloat(),
            onValueChange = {
                sliderValue = it.roundToInt()
            },
            onValueChangeFinished = {
                onSave(sliderValue)
            },
            steps = computedSteps,
            valueRange = valueRange.first.toFloat()..valueRange.last.toFloat(),
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


@Preview(showBackground = true)
@Composable
private fun SettingsItemWithIntSliderPreview() {
    LitheTheme {
        SettingsItemWithIntSlider(
            title = "Slider",
            initialValue = 0,
            onSave = {
            },
            valueRange = 0..5,
        )
    }
}


@Composable
fun SettingsItemWithFloatSlider(
    title: String,
    modifier: Modifier = Modifier,
    subtitleResId: Int? = null,
    initialValue: Float,
    onSave: (Float) -> Unit,
    floatRange: ClosedFloatingPointRange<Float>,
) {
    var sliderValue by remember(initialValue) {
        mutableFloatStateOf(initialValue)
    }

    val displaySubtitle = subtitleResId?.let {
        stringResource(it, sliderValue)
    } ?: sliderValue.toString()

    Row(
        modifier = modifier.fillMaxWidth(),
    ) {
        Row {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = displaySubtitle, textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Spacer(modifier = Modifier.width(24.dp))
        }



        Slider(
            value = sliderValue,
            onValueChange = {
                sliderValue = it
            },
            onValueChangeFinished = {
                onSave(sliderValue)
            },
            steps = 0,
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