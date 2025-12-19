package io.github.lycosmic.lithe.presentation.settings.appearance.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.lycosmic.lithe.data.model.AppThemeOption
import io.github.lycosmic.lithe.ui.theme.colorScheme


@Composable
fun ThemePreviewItem(
    appTheme: AppThemeOption,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
) {

    val colorScheme = colorScheme(appTheme)

    val primaryColor = colorScheme.primary // 主色调，用于边框和按钮
    val outlineColor = MaterialTheme.colorScheme.outlineVariant  // 边框色

    val onSurfaceColor = colorScheme.onSurface // 文本色，用于顶部栏
    val onSurfaceVariantColor = colorScheme.onSurfaceVariant
    val surfaceColor = colorScheme.surface // 容器色，用于卡片背景
    val surfaceContainerColor = colorScheme.surfaceContainer // 表面色，用于底部栏和内容区域

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            Modifier
                .clip(MaterialTheme.shapes.large)
                .clickable(enabled = !isSelected) {
                    onClick()
                }
                .background(surfaceColor)
                .border(
                    width = 4.dp,
                    color = if (isSelected) primaryColor else outlineColor,
                    shape = MaterialTheme.shapes.large
                )
                .padding(4.dp),
        ) {

            // --- 顶部状态栏/标题栏 ---
            Row(
                Modifier
                    .padding(vertical = 14.dp, horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .height(20.dp)
                        .width(80.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(onSurfaceColor)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // --- 选中状态的对勾图标 ---
                AnimatedVisibility(
                    visible = isSelected,
                    enter = scaleIn(tween(300), initialScale = 0.5f) +
                            fadeIn(tween(300)),
                    exit = fadeOut(tween(100))
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier
                            .size(26.dp),
                        tint = colorScheme.secondary
                    )
                }

                // 占位作用
                Box(modifier = Modifier.height(26.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))
            // --- 主要内容块 ---

            Box(
                Modifier
                    .padding(start = 8.dp)
                    .height(80.dp)
                    .width(70.dp)
                    .background(
                        surfaceContainerColor,
                        RoundedCornerShape(14.dp)
                    )
            )

            Spacer(modifier = Modifier.height(40.dp))
            // --- 底部按钮行 ---

            Row(
                Modifier
                    .width(130.dp)
                    .height(40.dp)
                    .background(
                        surfaceContainerColor
                    ),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(20.dp)
                        .background(primaryColor, CircleShape)
                )
                Box(
                    Modifier
                        .height(20.dp)
                        .width(60.dp)
                        .background(onSurfaceVariantColor, RoundedCornerShape(10.dp))
                )

            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- 主题名称 ---
        Text(
            text = stringResource(id = appTheme.labelResId),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ThemePreviewItemPreview() {
    ThemePreviewItem(appTheme = AppThemeOption.MERCURY, isSelected = true)
}