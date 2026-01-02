package io.github.lycosmic.lithe.log

import io.github.lycosmic.util.DomainLogger
import jakarta.inject.Inject

class TimberDomainLogger @Inject constructor() : DomainLogger {
    override fun i(tag: String?, throwable: Throwable?, message: () -> String) {
        logI(t = tag, e = throwable, message = message)
    }

    override fun d(tag: String?, throwable: Throwable?, message: () -> String) {
        logD(t = tag, e = throwable, message = message)
    }

    override fun w(tag: String?, throwable: Throwable?, message: () -> String) {
        logW(t = tag, e = throwable, message = message)
    }

    override fun e(tag: String?, throwable: Throwable?, message: () -> String) {
        logE(t = tag, e = throwable, message = message)
    }
}