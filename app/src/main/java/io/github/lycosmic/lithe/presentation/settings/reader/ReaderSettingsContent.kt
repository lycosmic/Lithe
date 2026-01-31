package io.github.lycosmic.lithe.presentation.settings.reader

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.lycosmic.data.settings.ReaderFontWeight
import io.github.lycosmic.domain.model.MyFontFamily
import io.github.lycosmic.lithe.R
import io.github.lycosmic.lithe.presentation.settings.components.SelectionChip
import io.github.lycosmic.lithe.presentation.settings.components.SettingsGroupTitle
import io.github.lycosmic.lithe.presentation.settings.components.SettingsSubGroupTitle
import io.github.lycosmic.lithe.ui.components.LitheSegmentedButton
import io.github.lycosmic.lithe.ui.components.OptionItem
import io.github.lycosmic.lithe.util.AppConstants
import io.github.lycosmic.lithe.util.extensions.displayNameResId
import io.github.lycosmic.lithe.util.length


/**
 * 阅读器设置页面内容
 */
@Composable
fun ReaderSettingsContent(
    modifier: Modifier = Modifier,
    currentFontId: String,
    currentFontSize: Float,
    currentFontWeight: Int,
    isReaderItalic: Boolean,
    currentLetterSpacing: Float,
    onFontIdChange: (String) -> Unit,
    onFontSizeChange: (Float) -> Unit,
    onFontWeightChange: (Int) -> Unit,
    onItalicChange: (Boolean) -> Unit,
    onLetterSpacingChange: (Float) -> Unit,
    fonts: List<MyFontFamily> = emptyList(),
    currentFontFamily: FontFamily,
) {
    LazyColumn(modifier) {
        item {
            Column {
                SettingsGroupTitle(
                    title = {
                        stringResource(id = R.string.reader_settings_font_group_title)
                    },
                    modifier = Modifier.padding(
                        top = 16.dp,
                        bottom = 8.dp,
                        start = 16.dp,
                        end = 16.dp
                    )
                )

                FontPreviewCard(
                    font = currentFontFamily,
                    weight = ReaderFontWeight.fromValue(currentFontWeight),
                    fontSize = currentFontSize,
                    isItalic = isReaderItalic,
                    letterSpacing = currentLetterSpacing,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
            }
        }

        // 字体系列
        item {
            Column {
                SettingsSubGroupTitle(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                    title = stringResource(id = R.string.reader_settings_font_family)
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    fonts.forEach { fontFamily ->
                        val text = when {
                            (MyFontFamily.Default.id == fontFamily.id) -> {
                                stringResource(id = R.string.reader_settings_font_family_default)
                            }

                            else -> fontFamily.displayName
                        }

                        SelectionChip(
                            text = text,
                            isSelected = fontFamily.id == currentFontId,
                            onClick = {
                                onFontIdChange(fontFamily.id)
                            },
                            fontFamily = fontFamily.family as? FontFamily ?: FontFamily.Default
                        )
                    }
                }
            }
        }


        // 字体粗细
        item {
            Column {
                SettingsSubGroupTitle(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                    title = stringResource(id = R.string.reader_settings_font_weight)
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    ReaderFontWeight.entries.forEach { fontWeight ->
                        SelectionChip(
                            text = stringResource(id = fontWeight.displayNameResId),
                            isSelected = fontWeight.value == currentFontWeight,
                            onClick = {
                                onFontWeightChange(fontWeight.value)
                            },
                            fontWeight = fontWeight.weight
                        )
                    }
                }
            }
        }

        // 字体样式
        item {
            Column {
                SettingsSubGroupTitle(
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp),
                    title = stringResource(id = R.string.reader_settings_font_style)
                )

                LitheSegmentedButton(
                    items = listOf(
                        OptionItem(
                            label = stringResource(R.string.reader_settings_font_style_normal),
                            value = false,
                            selected = !isReaderItalic
                        ),
                        OptionItem(
                            label = stringResource(R.string.reader_settings_font_style_italic),
                            value = true,
                            selected = isReaderItalic,
                            fontStyle = FontStyle.Italic
                        ),
                    ),
                    onClick = { isItalic ->
                        onItalicChange(isItalic)
                    },
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        // 字体大小和字间距
        item {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // 字体大小
                Row {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = stringResource(R.string.reader_settings_font_size))
                        Text(
                            text = stringResource(
                                R.string.reader_settings_font_size_format,
                                currentFontSize.toInt()
                            ), textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Slider(
                        value = currentFontSize,
                        onValueChange = {
                            onFontSizeChange(it)
                        },
                        steps = AppConstants.FONT_SIZE_INT_RANGE.length - 1,
                        valueRange = AppConstants.FONT_SIZE_INT_RANGE.first.toFloat()
                            .rangeTo(AppConstants.FONT_SIZE_INT_RANGE.last.toFloat()),
                        colors = SliderDefaults.colors(
                            activeTrackColor = MaterialTheme.colorScheme.secondary,
                            thumbColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                            activeTickColor = MaterialTheme.colorScheme.onSecondary,
                            inactiveTickColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }

                // 字间距
                Row {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = stringResource(R.string.reader_settings_font_letter_spacing))
                        Text(
                            text = stringResource(
                                R.string.reader_settings_font_letter_spacing_format,
                                currentLetterSpacing.toInt()
                            ), textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Slider(
                        value = currentLetterSpacing,
                        onValueChange = {
                            onLetterSpacingChange(it)
                        },
                        steps = AppConstants.LETTER_SPACING_INT_RANGE.length - 1,
                        valueRange = AppConstants.LETTER_SPACING_INT_RANGE.first.toFloat()
                            .rangeTo(AppConstants.LETTER_SPACING_INT_RANGE.last.toFloat()),
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
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))
        }


        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

