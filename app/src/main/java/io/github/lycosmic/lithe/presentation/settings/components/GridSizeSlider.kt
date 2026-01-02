package io.github.lycosmic.lithe.presentation.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.lycosmic.lithe.R
import io.github.lycosmic.model.AppConstraints

/**
 * 网格大小选择器
 */
@Composable
fun GridSizeSlider(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: IntRange = AppConstraints.GRID_SIZE_INT_RANGE,
) {
    val tipText = if (value == valueRange.first) {
        stringResource(R.string.auto)
    } else {
        stringResource(R.string.column_count, value)
    }

    Row(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = stringResource(R.string.grid_size))
            Text(text = tipText, textAlign = TextAlign.Center)
        }

        Spacer(modifier = Modifier.width(8.dp))

        Slider(
            value = value.toFloat(),
            onValueChange = {
                onValueChange(it.toInt())
            },
            steps = valueRange.last,
            valueRange = valueRange.first.toFloat().rangeTo(valueRange.last.toFloat()),
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
