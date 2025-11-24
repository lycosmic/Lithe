package io.github.lycosmic.lithe.presentation.reader

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.lycosmic.lithe.data.model.content.ReaderContent
import io.github.lycosmic.lithe.ui.components.CircularWavyProgressIndicator

@Composable
fun ReaderScreen(
    bookId: Long,
    viewModel: ReaderViewModel = hiltViewModel(),
) {
    // 初始加载书籍
    LaunchedEffect(bookId) {
        viewModel.loadBook(bookId)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()

    Scaffold(
        topBar = {
            // 阅读器的 TopBar 通常是隐藏的或极简的
            // 这里为了演示先显示一个简单的
            if (!uiState.isLoading && uiState.book != null) {
                ReaderTopBar(
                    title = uiState.book!!.title,
                )
            }
        },
        bottomBar = {
            if (!uiState.isLoading) {
                ReaderBottomBar(
                    current = uiState.currentChapterIndex,
                    total = uiState.spine.size,
                    onPrev = { viewModel.loadPrevChapter() },
                    onNext = { viewModel.loadNextChapter() }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFFBFBFB)) // 模拟纸张颜色
        ) {
            if (uiState.isLoading) {
                CircularWavyProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Text(
                    text = "错误: ${uiState.error}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                // *** 核心渲染逻辑 ***
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.currentContent) { content ->
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
                                        lineHeight = 1.8.em // 增加行高，提升阅读体验
                                    ),
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                            }

                            is ReaderContent.Image -> {
                                // 图片渲染暂时留空或显示占位
                                // 因为涉及到从 Zip 流加载图片，需要配合 Coil 的自定义 Fetcher
                                // 或者在 Parser 里把图片解压到缓存
                                Text(
                                    "[图片: ${content.relativePath}]",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }

                            is ReaderContent.Divider -> {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- 简单的子组件 ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderTopBar(title: String) {
    TopAppBar(
        title = { Text(title, style = MaterialTheme.typography.titleMedium) },
        navigationIcon = {
            IconButton(onClick = {}) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFBFBFB))
    )
}

@Composable
fun ReaderBottomBar(current: Int, total: Int, onPrev: () -> Unit, onNext: () -> Unit) {
    Surface(tonalElevation = 2.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onPrev, enabled = current > 0) { Text("上一章") }
            Text("${current + 1} / $total")
            TextButton(onClick = onNext, enabled = current < total - 1) { Text("下一章") }
        }
    }
}