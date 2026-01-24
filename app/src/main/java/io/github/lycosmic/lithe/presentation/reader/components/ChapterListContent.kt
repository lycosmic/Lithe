package io.github.lycosmic.lithe.presentation.reader.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.lycosmic.domain.model.BookChapter
import io.github.lycosmic.lithe.R
import io.github.lycosmic.lithe.ui.components.StyledText
import my.nanihadesuka.compose.InternalLazyColumnScrollbar
import my.nanihadesuka.compose.ScrollbarSelectionMode
import my.nanihadesuka.compose.ScrollbarSettings

/**
 * 章节列表
 */
@Composable
fun ChapterListContent(
    chapters: List<BookChapter>,
    currentChapterIndex: Int,
    currentChapterProgressText: String,
    onChapterClick: (Int) -> Unit,
    state: LazyListState = rememberLazyListState(),
) {
    val scrollbarSettings = ScrollbarSettings(
        thumbUnselectedColor = MaterialTheme.colorScheme.secondary,
        thumbSelectedColor = MaterialTheme.colorScheme.secondary.copy(0.8f),
        hideDelayMillis = 2000,
        durationAnimationMillis = 300,
        selectionMode = ScrollbarSelectionMode.Thumb,
        thumbThickness = 8.dp,
        scrollbarPadding = 4.dp
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .statusBarsPadding()
    ) {
        // --- 章节标题 ---
        Text(
            text = stringResource(R.string.chapter_list),
            modifier = Modifier.padding(start = 32.dp, top = 16.dp, bottom = 8.dp),
            style = MaterialTheme.typography.titleLarge
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        // --- 章节列表 ---
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                state = state
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                items(
                    items = chapters,
                ) { chapter ->
                    // 列表项指针事件
                    val interactionSource = remember { MutableInteractionSource() }

                    val isPressed by interactionSource.collectIsPressedAsState()

                    val currentChapterSelected = chapter.index == currentChapterIndex

                    // 章节名是否展开：如果正在被按下或者当前章节被选中，则展开
                    val isChapterNameExpanded = isPressed || currentChapterSelected

                    NavigationDrawerItem(
                        label = {
                            Row {
                                StyledText(
                                    text = chapter.title,
                                    modifier = Modifier
                                        .weight(1f)
                                        .animateContentSize(),
                                    maxLines = if (isChapterNameExpanded) 10 else 1,
                                )
                                if (currentChapterSelected) {
                                    Spacer(modifier = Modifier.width(18.dp))
                                    StyledText(text = currentChapterProgressText)
                                }
                            }

                        },
                        selected = chapter.index == currentChapterIndex, // 高亮当前章节
                        onClick = { onChapterClick(chapter.index) },
                        modifier = Modifier
                            .padding(NavigationDrawerItemDefaults.ItemPadding),
                        interactionSource = interactionSource,
                    )
                }
            }

            InternalLazyColumnScrollbar(
                state = state,
                settings = scrollbarSettings
            )
        }
    }
}