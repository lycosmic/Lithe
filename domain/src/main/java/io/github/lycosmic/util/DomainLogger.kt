package io.github.lycosmic.util

/**
 * 领域层日志接口
 */
interface DomainLogger {
    fun i(tag: String? = null, throwable: Throwable? = null, message: () -> String)
    fun d(tag: String? = null, throwable: Throwable? = null, message: () -> String)
    fun w(tag: String? = null, throwable: Throwable? = null, message: () -> String)
    fun e(tag: String? = null, throwable: Throwable? = null, message: () -> String)
}