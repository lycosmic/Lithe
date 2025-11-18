package io.github.lycosmic.lithe.presentation.browse

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun BrowseScreen(modifier: Modifier = Modifier, onGoToSettings: () -> Unit) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "浏览",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    Button(
                        onClick = onGoToSettings
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "设置"
                        )
                    }
                }
            )
        }
    ) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "这里是书浏览列表"
            )
        }
    }
}
