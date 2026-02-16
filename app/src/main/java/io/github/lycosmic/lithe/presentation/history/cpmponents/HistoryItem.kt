package io.github.lycosmic.lithe.presentation.history.cpmponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.lycosmic.domain.model.ReadHistory
import io.github.lycosmic.lithe.ui.components.AsyncCoverImage
import io.github.lycosmic.lithe.ui.components.StyledText
import io.github.lycosmic.lithe.util.clickableNoRipple
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun LazyItemScope.HistoryItem(
    history: ReadHistory,
    onBodyClick: () -> Unit,
    onTitleClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val date = rememberSaveable(history) { Date(history.time) }
    val pattern = rememberSaveable { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .animateItem()
            .fillMaxWidth()
            .clickable(enabled = true) {
                onBodyClick()
            }
            .padding(top = 6.dp, bottom = 6.dp, start = 16.dp, end = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .height(90.dp)
                    .width(60.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceContainerLow,
                        RoundedCornerShape(10.dp)
                    )
            ) {
                if (history.book.coverPath != null) {
                    AsyncCoverImage(
                        path = history.book.coverPath!!,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp))
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .fillMaxWidth(0.7f)
                            .aspectRatio(1f),
                        tint = MaterialTheme.colorScheme.surfaceContainerHigh
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxHeight()
            ) {
                StyledText(
                    text = history.book.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickableNoRipple(
                            onClick = onTitleClick
                        ),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 21.sp
                    ),
                    maxLines = 2
                )
                StyledText(
                    text = pattern.format(date),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    maxLines = 1
                )
            }
        }

        Spacer(Modifier.width(10.dp))

        IconButton(
            onClick = onDeleteClick
        ) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}