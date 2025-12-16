package io.github.lycosmic.lithe.utils

import android.app.Application
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.StringRes
import io.github.lycosmic.lithe.extension.logE

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
    fun show(@StringRes messageResId: Int, duration: Int = Toast.LENGTH_SHORT) {
        try {
            show(appContext.getString(messageResId), duration)
        } catch (e: Exception) {
            logE(e = e) {
                "Toast 显示异常，可能未初始化 Context"
            }
        }
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