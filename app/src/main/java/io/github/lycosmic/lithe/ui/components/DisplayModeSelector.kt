package io.github.lycosmic.lithe.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.lycosmic.lithe.data.DisplayMode

@Composable
fun DisplayModeSelector(
    currentMode: DisplayMode,
    onModeChanged: (DisplayMode) -> Unit,
    modifier: Modifier = Modifier,
    selectedWeight: Float = 1.5f,
    unselectedWeight: Float = 1f,
    durationMillis: Int = 300
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        DisplayMode.entries.forEachIndexed { index, mode ->
            val isSelected = currentMode == mode

            val animateWeight by animateFloatAsState(
                targetValue = if (isSelected) selectedWeight else unselectedWeight,
                animationSpec = tween(durationMillis = durationMillis, easing = LinearEasing)
            )

            SegmentedButton(
                selected = isSelected,
                onClick = { onModeChanged(mode) },
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = DisplayMode.entries.size
                ),
                label = { Text(mode.label) },
                modifier = Modifier.weight(weight = animateWeight)
            )
        }
    }
}


@Preview
@Composable
fun DisplayModeSelectorPreview() {
    var mode by remember { mutableStateOf(DisplayMode.List) }
    DisplayModeSelector(
        currentMode = mode,
        onModeChanged = { displayMode ->
            mode = displayMode
        }
    )
}