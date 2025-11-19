package io.github.lycosmic.lithe.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// 菜单项
data class ActionItem(
    val text: String,
    val onClick: () -> Unit,
    val icon: ImageVector? = null, // 支持图标
    val isDestructive: Boolean = false, // 是否是危险操作, 变红
    // 特定颜色
    val containerColor: Color? = null,
    val contentColor: Color? = null,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LitheActionSheet(
    showBottomSheet: Boolean,
    onDismissRequest: () -> Unit,
    // 分组列表, 外层是分组, 内层是菜单项
    groups: List<List<ActionItem>>,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(),
    cornerRadius: Dp = 12.dp, // 菜单项圆角大小
    groupSpacing: Dp = 24.dp, // 组间距
    contentBottomPadding: Dp = 32.dp, // 底部间距
    itemHorizontalPadding: Dp = 12.dp, // 菜单项水平间距
    itemVerticalPadding: Dp = 2.dp, // 菜单项垂直间距
    defaultContainerColor: Color = MaterialTheme.colorScheme.surfaceContainer, // 默认容器颜色
    defaultContentColor: Color = MaterialTheme.colorScheme.onSurface, // 默认内容颜色
) {
    if (showBottomSheet) {
        ModalBottomSheet(
            modifier = modifier,
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
        ) {
            Column(
                modifier = Modifier.padding(bottom = contentBottomPadding),
            ) {
                groups.forEachIndexed { groupIndex, groupItems ->
                    // 如果不是第一组, 在组之间添加 Spacer
                    if (groupIndex > 0) {
                        Spacer(modifier = Modifier.height(height = groupSpacing))
                    }

                    // 遍历组内的每一项
                    groupItems.forEachIndexed { itemIndex, item ->
                        val isFirst = itemIndex == 0
                        val isLast = itemIndex == groupItems.lastIndex

                        val shape = when {
                            isFirst && isLast -> RoundedCornerShape(size = cornerRadius) // 只有一个项:全圆
                            isFirst -> RoundedCornerShape(
                                topStart = cornerRadius,
                                topEnd = cornerRadius,
                            ) // 第一项:上圆
                            isLast -> RoundedCornerShape(
                                bottomStart = cornerRadius,
                                bottomEnd = cornerRadius,
                            ) // 最后一项:下圆
                            else -> RectangleShape // 中间项:直角
                        }

                        // 优先级: Destructive > Item Specific > Default
                        val finalContentColor = when {
                            // 如果是破坏性操作(如删除), 文字变红
                            item.isDestructive -> MaterialTheme.colorScheme.error
                            item.contentColor != null -> item.contentColor
                            else -> defaultContentColor
                        }

                        val finalContainerColor = when {
                            item.isDestructive -> MaterialTheme.colorScheme.errorContainer
                            item.contentColor != null -> item.containerColor!!
                            else -> defaultContainerColor
                        }


                        NavigationDrawerItem(
                            label = {
                                Text(
                                    text = item.text,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    color = finalContentColor
                                )
                            },
                            shape = shape,
                            selected = true,
                            onClick = {
                                onDismissRequest()
                                item.onClick()
                            },
                            modifier = Modifier.padding(
                                horizontal = itemHorizontalPadding,
                                vertical = itemVerticalPadding
                            ),
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = finalContainerColor,
                                selectedTextColor = finalContentColor,
                                selectedIconColor = finalContentColor
                            )
                        )
                    }
                }
            }
        }
    }

}