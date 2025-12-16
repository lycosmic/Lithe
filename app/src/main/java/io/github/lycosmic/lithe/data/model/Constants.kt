package io.github.lycosmic.lithe.data.model

object Constants {
    /**
     * StateFlow 停止订阅超时时间(毫秒)
     * 用于 StateIn 函数的 stopTimeoutMillis 参数
     */
    const val STATE_FLOW_STOP_TIMEOUT_MILLIS: Long = 5000L

    /**
     * 双击返回键的间隔时间(毫秒)
     */
    const val DOUBLE_CLICK_BACK_INTERVAL_MILLIS: Long = 2000L
}