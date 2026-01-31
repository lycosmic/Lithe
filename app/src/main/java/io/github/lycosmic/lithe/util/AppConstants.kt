package io.github.lycosmic.lithe.util


object AppConstants {
    /**
     * StateFlow 停止订阅超时时间(毫秒)
     */
    const val STATE_FLOW_STOP_TIMEOUT = 5000L

    /**
     * 双击返回键的间隔时间(毫秒)
     */
    const val DOUBLE_CLICK_BACK_INTERVAL = 2000L

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
     * 字体大小的范围
     */
    val FONT_SIZE_INT_RANGE = 10..35

    /**
     * 字间距的范围
     */
    val LETTER_SPACING_INT_RANGE = -8..16
}