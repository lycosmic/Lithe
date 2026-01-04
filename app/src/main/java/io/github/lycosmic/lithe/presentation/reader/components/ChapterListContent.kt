package io.github.lycosmic.lithe.presentation.reader.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
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

/**
 * 章节列表
 */
@Composable
fun ChapterListContent(
    chapters: List<BookChapter>,
    currentChapterIndex: Int,
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
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleLarge
        )

        HorizontalDivider()

        // --- 章节列表 ---
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(
                items = chapters,
            ) { chapter ->
                NavigationDrawerItem(
                    label = { Text(chapter.title) },
                    selected = chapter.index == currentChapterIndex, // 高亮当前章节
                    onClick = { onChapterClick(chapter.index) },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    }
}