package io.github.lycosmic.lithe.presentation.settings.library.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import io.github.lycosmic.lithe.R
import kotlinx.coroutines.delay


/**
 * 创建书籍分类对话框
 */
@Composable
fun CreateCategoryDialog(
    onDismissRequest: () -> Unit, // 点击取消或对话框外部区域
    onConfirm: (String) -> Unit // 点击确定
) {

    var text by remember { mutableStateOf("") }

    // 是否处于错误状态，显示红框
    var isError by remember { mutableStateOf(false) }

    // 用于自动弹出键盘
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // 输入框获取焦点并弹出键盘
    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
        keyboardController?.show()
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(id = R.string.create_category)) },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = {
                    text = it
                    if (it.isNotBlank()) isError = false
                },
                label = { Text(stringResource(id = R.string.category_name)) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester), // 绑定聚焦器
                isError = isError,
                supportingText = { // 错误提示
                    if (isError) {
                        Text(text = stringResource(id = R.string.category_name_cannot_be_empty))
                    }
                }
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (text.isNotBlank()) {
                        isError = false
                        onConfirm(text)
                    } else {
                        // 触发错误状态
                        isError = true
                    }
                },
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}