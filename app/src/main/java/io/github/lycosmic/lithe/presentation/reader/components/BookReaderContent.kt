package io.github.lycosmic.lithe.presentation.reader.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import coil3.compose.AsyncImage
import io.github.lycosmic.lithe.presentation.reader.ReaderContent

/**
 * 阅读内容
 */
@Composable
fun BookReaderContent(
    contents: List<ReaderContent>,
    onContentClick: () -> Unit,
    listState: LazyListState
) {
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                onContentClick()
            }
    ) {
        items(contents) { content ->
            when (content) {
                is ReaderContent.Title -> {
                    Text(
                        text = content.text,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp, top = 8.dp)
                    )
                }

                is ReaderContent.Paragraph -> {
                    Text(
                        text = content.text,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 1.8.em
                        ),
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                is ReaderContent.Image -> {
                    AsyncImage(
                        model = content.path,
                        contentDescription = null,
                        alignment = Alignment.TopStart,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                is ReaderContent.Divider -> {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                }
            }
        }
    }
}