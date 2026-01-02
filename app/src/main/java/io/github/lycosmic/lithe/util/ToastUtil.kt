package io.github.lycosmic.lithe.util

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.StringRes
import io.github.lycosmic.lithe.log.logE

/**
 * 全局 Toast 工具
 */
object ToastUtil {

    private var toast: Toast? = null
    private lateinit var appContext: Context

    private val mainHandler = Handler(Looper.getMainLooper())

    /**
     * 在 Application 中初始化
     */
    fun init(application: Application) {
        appContext = application.applicationContext
    }


    /**
     * 显示 Toast
     */
    fun show(message: String?, duration: Int = Toast.LENGTH_SHORT) {
        if (message.isNullOrBlank()) {
            return
        }

        // 确保在主线程中显示 Toast
        if (Looper.myLooper() == Looper.getMainLooper()) {
            showInternal(message = message, duration = duration)
        } else {
            mainHandler.post { showInternal(message, duration) }
        }
    }


    /**
     * 显示 Toast (资源 ID)
     */
    fun show(
        @StringRes messageResId: Int,
        duration: Int = Toast.LENGTH_SHORT,
    ) {
        try {
            show(appContext.getString(messageResId), duration)
        } catch (e: Exception) {
            logE(e = e) {
                "Toast 显示异常，可能未初始化 Context"
            }
        }
    }

    /**
     * 资源 ID 显示 Toast (格式化)
     */
    fun show(
        @StringRes messageResId: Int,
        vararg formatArgs: Any,
    ) {
        val formattedText = appContext.getString(messageResId, *formatArgs)
        show(formattedText, Toast.LENGTH_SHORT)
    }

    private fun showInternal(message: String, duration: Int) {
        try {
            // 取消旧的 Toast
            toast?.cancel()

            // 创建新的
            toast = Toast.makeText(appContext, message, duration)
            toast?.show()
        } catch (e: Exception) {
            logE(e = e) {
                "Toast 显示异常"
            }
        }
    }
}

/**
 * 扩展函数：方便显示 Toast
 */
fun String.toast(duration: Int = Toast.LENGTH_SHORT) {
    ToastUtil.show(this, duration)
}


fun Int.toast(duration: Int = Toast.LENGTH_SHORT) {
    ToastUtil.show(this, duration)
}

fun Int.toast(vararg formatArgs: Any) {
    ToastUtil.show(this, *formatArgs)
}