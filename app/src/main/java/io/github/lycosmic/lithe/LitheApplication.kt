package io.github.lycosmic.lithe

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.github.lycosmic.lithe.extension.logI
import io.github.lycosmic.lithe.utils.ToastUtil
import timber.log.Timber

@HiltAndroidApp
class LitheApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initLogger()
        initToast()
        logI {
            "Application 初始化成功"
        }
    }

    private fun initLogger() {
        if (BuildConfig.DEBUG) {
            // 自动使用类名作为 TAG
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String? {
                    return "${LOG_PREFIX}-${super.createStackElementTag(element)}"
                }
            })
        }
    }

    private fun initToast() {
        ToastUtil.init(this@LitheApplication)
    }

    companion object {
        // 统一的日志前缀
        private const val LOG_PREFIX = "LitheLog"
    }
}