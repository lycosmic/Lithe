package io.github.lycosmic.lithe

import io.github.lycosmic.lithe.util.FormatUtils.formatProgress
import junit.framework.TestCase.assertEquals
import org.junit.Test

class ProgressFormatterTest {

    @Test
    fun `test default digits (1 decimal place)`() {
        // 0.5 * 100 = 50.0
        assertEquals("50.0%", formatProgress(0.5f))

        // 0.123 * 100 = 12.3
        assertEquals("12.3%", formatProgress(0.123f))

        // 0.0 * 100 = 0.0
        // 变化点：为了UI稳定，0也要显示一位小数
        assertEquals("0.0%", formatProgress(0.0f))
    }

    @Test
    fun `test custom digits`() {
        // digits = 0 (取整)
        // 只有显式要求 digits=0 时，才不显示小数
        assertEquals("43%", formatProgress(0.429f, digits = 0))

        // digits = 2
        assertEquals("12.34%", formatProgress(0.1234f, digits = 2))

        // digits = 3
        assertEquals("33.333%", formatProgress(0.333333f, digits = 3))
    }

    @Test
    fun `test rounding logic`() {
        // 默认保留1位小数
        // 0.5555 * 100 = 55.55 -> 四舍五入(HALF_UP) -> 55.6%
        assertEquals("55.6%", formatProgress(0.5555f))

        // 0.5554 * 100 = 55.54 -> 向下舍 -> 55.5%
        assertEquals("55.5%", formatProgress(0.5554f))

        // 0 位小数时的四舍五入
        // 0.666 * 100 = 66.6 -> 67%
        assertEquals("67%", formatProgress(0.666f, digits = 0))
    }

    @Test
    fun `test boundaries and clamping`() {
        // < 0% (被钳制为 0)
        // 变化点：钳制后依然要遵循默认的 digits=1 格式
        assertEquals("0.0%", formatProgress(-0.5f))
        assertEquals("0.0%", formatProgress(-0.00001f))

        // 100%
        assertEquals("100.0%", formatProgress(1.0f))

        // > 100% (被钳制为 100)
        assertEquals("100.0%", formatProgress(1.2f))
        assertEquals("100.0%", formatProgress(1.5f))
        assertEquals("100.0%", formatProgress(200f))

        // 极小值 (接近0但大于0)
        // 0.000001 * 100 = 0.0001 -> 四舍五入保留1位 -> 0.0
        assertEquals("0.0%", formatProgress(0.000001f))
    }
}