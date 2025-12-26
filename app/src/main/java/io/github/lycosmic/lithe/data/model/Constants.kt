package io.github.lycosmic.lithe.data.model

import androidx.compose.ui.unit.dp
import io.github.lycosmic.lithe.R

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

    /**
     * 颜色的最大值
     */
    const val MAX_COLOR_VALUE = 255

    /**
     * 网格大小的范围
     * 0 表示自动
     */
    val GRID_SIZE_INT_RANGE = 0..10

    /**
     * 最小的网格项宽度
     */
    val MIN_GRID_ITEM_WIDTH = 170.dp

    /**
     * 默认的分类 ID
     */
    const val DEFAULT_CATEGORY_ID = 1L

    /**
     * 默认的分类名称
     */
    val DEFAULT_CATEGORY_NAME: Int = R.string.default_category_name
}