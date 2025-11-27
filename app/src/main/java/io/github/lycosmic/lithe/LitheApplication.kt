package io.github.lycosmic.lithe

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class LitheApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initLogger()
    }

    private fun initLogger() {
        // 自动使用类名作为 TAG
        Timber.plant(object : Timber.DebugTree() {
            override fun createStackElementTag(element: StackTraceElement): String? {
                return "${LOG_PREFIX}-${super.createStackElementTag(element)}"
            }
        })
    }

    companion object {
        // 统一的日志前缀
        private const val LOG_PREFIX = "LitheLog"
    }
}