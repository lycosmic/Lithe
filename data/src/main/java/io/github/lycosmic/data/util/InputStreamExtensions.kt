package io.github.lycosmic.data.util

import java.io.InputStream

/**
 * 跳过指定字节数
 */
fun InputStream.skipFully(n: Long): Long {
    var totalSkipped = 0L

    while (totalSkipped < n) {
        val skipped = this.skip(n - totalSkipped)
        if (skipped == 0L) {
            // 到达末尾或者是流阻塞
            // 尝试读一个字节来看看是不是真没数据了
            if (this.read() == -1) {
                break
            } else {
                totalSkipped++
            }
        } else {
            totalSkipped += skipped
        }
    }
    return totalSkipped
}