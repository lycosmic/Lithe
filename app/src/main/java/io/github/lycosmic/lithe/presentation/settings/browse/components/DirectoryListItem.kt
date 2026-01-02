package io.github.lycosmic.lithe.presentation.settings.browse.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.lycosmic.lithe.R
import io.github.lycosmic.model.Directory

@Composable
fun LazyItemScope.DirectoryListItem(
    directory: Directory,
    onDeleteClick: (Directory) -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        modifier = modifier
            .fillMaxWidth()
            .animateItem(
                fadeInSpec = spring(stiffness = Spring.StiffnessMedium),
                placementSpec = spring(stiffness = Spring.StiffnessLow),
                fadeOutSpec = spring(stiffness = Spring.StiffnessMedium)
            ),
        headlineContent = {
            Text(text = directory.path)
        },
        supportingContent = {
            Text(text = directory.root)
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .clip(shape = CircleShape)
                    .background(color = MaterialTheme.colorScheme.surfaceContainer)
                    .padding(all = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Folder,
                    contentDescription = null
                )
            }
        },
        trailingContent = {
            IconButton(
                onClick = {
                    onDeleteClick(directory)
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Clear,
                    contentDescription = stringResource(R.string.remove_directory)
                )
            }
        }
    )
}
