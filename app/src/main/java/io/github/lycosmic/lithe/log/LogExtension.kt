package io.github.lycosmic.lithe.log

import io.github.lycosmic.lithe.BuildConfig
import timber.log.Timber

/**
 * Timber 日志扩展, 惰性求值 + 可选 Tag
 */
inline fun logV(t: String? = null, e: Throwable? = null, crossinline message: () -> String) {
    if (BuildConfig.DEBUG) {
        t?.let {
            Timber.tag(t)
        }
        Timber.v(e, message())
    }
}

inline fun logD(t: String? = null, e: Throwable? = null, crossinline message: () -> String) {
    if (BuildConfig.DEBUG) {
        t?.let {
            Timber.tag(t)
        }
        Timber.d(e, message())
    }
}

inline fun logI(t: String? = null, e: Throwable? = null, crossinline message: () -> String) {
    if (BuildConfig.DEBUG) {
        t?.let {
            Timber.tag(t)
        }
        Timber.i(e, message())
    }
}

inline fun logW(t: String? = null, e: Throwable? = null, crossinline message: () -> String) {
    t?.let {
        Timber.tag(t)
    }
    Timber.w(e, message())
}

inline fun logE(t: String? = null, e: Throwable? = null, crossinline message: () -> String) {
    t?.let {
        Timber.tag(t)
    }
    Timber.e(e, message())
}