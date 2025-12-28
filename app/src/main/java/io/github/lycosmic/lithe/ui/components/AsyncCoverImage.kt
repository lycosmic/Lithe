package io.github.lycosmic.lithe.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade

/**
 * 异步加载图片，默认使用淡入淡出效果
 */
@Composable
fun AsyncCoverImage(
    path: String, // 封面本地路径
    animationDurationMillis: Int = 100,
    contentDescription: String? = null,
    alpha: Float = 1f,
    modifier: Modifier
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(path)
            .crossfade(animationDurationMillis).build(),
        contentDescription = contentDescription,
        modifier = modifier,
        alpha = alpha,
        contentScale = ContentScale.Crop
    )
}