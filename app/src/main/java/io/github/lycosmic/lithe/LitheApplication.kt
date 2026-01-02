package io.github.lycosmic.lithe

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.github.lycosmic.lithe.log.logI
import io.github.lycosmic.lithe.util.ToastUtil
import io.github.lycosmic.repository.CategoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class LitheApplication : Application() {

    @Inject
    lateinit var categoryRepository: CategoryRepository

    override fun onCreate() {
        super.onCreate()
        logI {
            "Application 初始化成功"
        }
        initLogger()
        initToast()

        initDefaultCategory()
    }


    private fun initLogger() {
        if (BuildConfig.DEBUG) {
            // 自动使用类名作为 TAG
            Timber.plant(object : Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String {
                    return "${LOG_PREFIX}-${super.createStackElementTag(element)}"
                }
            })
        }
    }

    private fun initToast() {
        ToastUtil.init(this@LitheApplication)
    }

    /**
     * 初始化默认分类
     */
    private fun initDefaultCategory() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                categoryRepository.ensureDefaultCategoryExists()
            } catch (e: Exception) {
                logI { "初始化默认分类失败: ${e.message}" }
            }
        }
    }

    companion object {
        // 统一的日志前缀
        private const val LOG_PREFIX = "LitheLog"
    }
}