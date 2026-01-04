package io.github.lycosmic.lithe.presentation.reader.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.lycosmic.domain.model.BookChapter
import io.github.lycosmic.lithe.R
import io.github.lycosmic.lithe.ui.components.StyledText

/**
 * 章节列表
 */
@Composable
fun ChapterListContent(
    chapters: List<BookChapter>,
    currentChapterIndex: Int,
    currentChapterProgressText: String,
    onChapterClick: (Int) -> Unit,
) {
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            items(
                items = chapters,
            ) { chapter ->
                NavigationDrawerItem(
                    label = {
                        Row {
                            StyledText(
                                text = chapter.title,
                                modifier = Modifier.weight(1f),
                                maxLines = 1
                            )
                            if (chapter.index == currentChapterIndex) {
                                Spacer(modifier = Modifier.width(18.dp))
                                StyledText(text = currentChapterProgressText)
                            }
                        }

                    },
                    selected = chapter.index == currentChapterIndex, // 高亮当前章节
                    onClick = { onChapterClick(chapter.index) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    }
}